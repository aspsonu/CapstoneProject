import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../../../services/auth.service';
import { environment } from '../../../../../environments/environment';

interface MaintenanceEvent {
  id?: number;
  vehicleNumber: string;
  date: string;
  maintenanceCost: number;
  maintenanceDescription: string;
  isEditing?: boolean;
}

@Component({
  selector: 'app-maintenance-events',
  templateUrl: './maintenance-events.component.html',
  styleUrls: ['./maintenance-events.component.css'],
  standalone: false
})
export class MaintenanceEventsComponent implements OnInit {
  maintenanceEvents: MaintenanceEvent[] = [];
  selectedEvent: MaintenanceEvent | null = null;
  showModal: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  modalErrorMessage: string = '';
  modalSuccessMessage: string = '';
  userId: string = '';
  isAdmin: boolean = false;

  startDate: string = '';
  endDate: string = '';
  filteredEvents: MaintenanceEvent[] = [];


  constructor(private http: HttpClient, private authService: AuthService) {
    this.userId = this.authService.getUserId();
    //this.isAdmin = this.authService.getRole() === 'ADMIN';
    this.isAdmin = this.checkIfAdmin();
  }

  ngOnInit(): void {
    this.userId = this.authService.getUserId();
    if (!this.userId) {
      this.showTemporaryMessage('errorMessage', 'User ID is missing. Please log in again.');
      return;
    }
    //this.isAdmin = this.authService.getRole() === 'ADMIN';
    this.isAdmin = this.checkIfAdmin();
    this.fetchMaintenanceEvents();
  }

  private checkIfAdmin(): boolean {
    const role = this.authService.getRole();
    return role === 'ADMIN' || role === 'ROOT_ADMIN';
  }

  getHeaders() {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  }

  fetchMaintenanceEvents() {
    if (!this.userId) {
      this.showTemporaryMessage('errorMessage', 'User ID is missing.');
      return;
    }

    this.http.get<MaintenanceEvent[]>(`${environment.apiBaseUrl}/api/admin/maintenance/list/${this.userId}/${this.isAdmin}`, 
      { headers: this.getHeaders() }
    ).subscribe({
      next: (data) => {
        this.maintenanceEvents = data.map(event => ({ ...event, isEditing: false }));
        this.filteredEvents = [...this.maintenanceEvents];
      },
      error: (errorResponse) => {
        this.showTemporaryMessage('errorMessage', errorResponse?.error?.message || 'Failed to load maintenance events.');
      }
    });
  }

  applyDateFilter() {
    if (!this.startDate || !this.endDate) {
      this.filteredEvents = [...this.maintenanceEvents];
      return;
    }
  
    const start = new Date(this.startDate);
    const end = new Date(this.endDate);
  
    this.filteredEvents = this.maintenanceEvents.filter(event => {
      const eventDateOnly = new Date(event.date.split('T')[0]); // 💥 fix here
      return eventDateOnly >= start && eventDateOnly <= end;
    });
  }
  
  
  clearDateFilter() {
    this.startDate = '';
    this.endDate = '';
    this.filteredEvents = [...this.maintenanceEvents];
  }
  

  openModal() {
    this.selectedEvent = { vehicleNumber: '', date: '', maintenanceCost: 0, maintenanceDescription: '' };
    this.showModal = true;
    this.modalSuccessMessage = '';
    this.modalErrorMessage = '';
  }

  closeModal() {
    this.showModal = false;
    this.selectedEvent = null;
  }

  validateEvent(event: MaintenanceEvent): string | null {
    if (!event.vehicleNumber.trim()) return "Vehicle number is required.";
    if (!event.date.trim()) return "Date is required.";
    if (!event.maintenanceCost || event.maintenanceCost <= 0) return "Maintenance cost must be greater than 0.";
    if (!event.maintenanceDescription.trim()) return "Maintenance description is required.";
    return null;
  }

  saveEvent() {
    if (!this.selectedEvent) return;

    const validationError = this.validateEvent(this.selectedEvent);
    if (validationError) {
      this.showTemporaryMessage('modalErrorMessage', validationError);
      return;
    }

    const apiUrl = `${environment.apiBaseUrl}/api/admin/maintenance/add/${this.userId}`;

    this.http.post<{ message?: string; error?: string }>(apiUrl, this.selectedEvent, { headers: this.getHeaders() })
      .subscribe({
        next: (response) => {
          if (response.error) {
            this.showTemporaryMessage('modalErrorMessage', response.error);
            return;
          }
          this.fetchMaintenanceEvents();
          //this.showTemporaryMessage('modalSuccessMessage', response.message || 'Event added successfully!');
          this.modalSuccessMessage = response.message || 'Event added successfully!';
          setTimeout(() => this.closeModal(), 2000);
        },
        error: (errorResponse) => {
          //this.modalErrorMessage = errorResponse?.error?.message || "Failed to add event.";
          this.showTemporaryMessage('modalErrorMessage', errorResponse?.error?.message || "Failed to add event.");
        }
      });
  }

  toggleEdit(event: MaintenanceEvent) {
    if (event.isEditing) {
      this.updateEvent(event);
    } else {
      event.isEditing = true;
    }
  }

  updateEvent(event: MaintenanceEvent) {
    if (!event.id) return;

    const validationError = this.validateEvent(event);
    if (validationError) {
      this.showTemporaryMessage('errorMessage', validationError);
      return;
    }

    const apiUrl = `${environment.apiBaseUrl}/api/admin/maintenance/update/${event.id}/${this.userId}/${this.isAdmin}`;

    this.http.put<{ message?: string; error?: string }>(apiUrl, event, { headers: this.getHeaders() })
      .subscribe({
        next: (response) => {
          if (response.error) {
            this.showTemporaryMessage('errorMessage', response.error);
            return;
          }
          event.isEditing = false;
          this.showTemporaryMessage('successMessage', response.message || 'Event updated successfully!');
        },
        error: (errorResponse) => {
          this.showTemporaryMessage('errorMessage', errorResponse.error || errorResponse?.error?.message || 'Failed to update event.');
        }
      });
  }

  deleteEvent(eventId: number | undefined) {
    if (!eventId) return;
    if (!confirm('Are you sure you want to delete this event?')) return;

    this.http.delete<{ message?: string; error?: string }>(
      `${environment.apiBaseUrl}/api/admin/maintenance/delete/${eventId}/${this.userId}/${this.isAdmin}`, 
      { headers: this.getHeaders() }
    ).subscribe({
      next: (response) => {
        if (response.error) {
          this.showTemporaryMessage('errorMessage', response.error);
          return;
        }
        this.fetchMaintenanceEvents();
        this.showTemporaryMessage('successMessage', response.message || 'Event deleted successfully!');
      },
      error: (errorResponse) => {
        this.showTemporaryMessage('errorMessage', errorResponse?.error?.message || 'Failed to delete event.'); 
      }
    });
  }

  showTemporaryMessage(type: 'errorMessage' | 'successMessage' | 'modalErrorMessage' | 'modalSuccessMessage', message: string) {
    this[type] = message;
    setTimeout(() => this.clearMessage(type), 3000);
  }

  clearMessage(type: 'errorMessage' | 'successMessage' | 'modalErrorMessage' | 'modalSuccessMessage') {
    this[type] = '';
  }
  
  cancelEdit(event: MaintenanceEvent) {
    // Disable editing and restore original values
    event.isEditing = false;
    this.fetchMaintenanceEvents(); // Reloads data from the backend to reset changes
  }
}
