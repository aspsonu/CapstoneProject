import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../../../services/auth.service';
import { environment } from '../../../../../environments/environment';

interface FuelingEvent {
  id?: number;
  vehicleNumber: string;
  date: string;
  fuelCost: number;
  fuelAdded: number;
  currentMileage: number;
  isEditing?: boolean;
}

@Component({
  selector: 'app-user-fueling',
  standalone: false,
  templateUrl: './user-fueling.component.html',
  styleUrl: './user-fueling.component.css'
})
export class UserFuelingComponent implements OnInit{
  fuelingEvents: FuelingEvent[] = [];
  errorMessage: string = '';
  userId: string = '';
  isUser: boolean = false;
  successMessage: string = '';
  modalErrorMessage: string = '';
  modalSuccessMessage: string = '';
  selectedEvent: FuelingEvent | null = null;
  showModal: boolean = false;

  constructor(private http: HttpClient, private authService: AuthService) {
    this.userId = this.authService.getUserId();
    this.isUser = this.authService.getRole()?.toUpperCase() === 'USER' ? false : true;
  }

  ngOnInit(): void {
    this.userId = this.authService.getUserId();
    if (!this.userId) {
      this.showTemporaryMessage('errorMessage', 'User ID is missing. Please log in again.');
      return;
    }
    this.isUser = this.authService.getRole()?.toUpperCase() === 'USER' ? false : true;
    this.fetchFuelingEvents();
  }

  getHeaders() {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  }

  fetchFuelingEvents() {
    if (!this.userId) {
      this.showTemporaryMessage('errorMessage', 'User ID is missing.');
      return;
    }
    this.http.get<FuelingEvent[]>(`${environment.apiBaseUrl}/api/admin/fueling/list/${this.userId}/${this.isUser}`, 
      { headers: this.getHeaders() }
    ).subscribe({
      next: (data) => {
        this.fuelingEvents = data.map(event => ({ ...event, isEditing: false }));
      },
      error: (errorResponse) => {
        this.showTemporaryMessage('errorMessage', errorResponse?.error?.message || 'Failed to load maintenance events.');
      }
    });
  }

  cancelEdit(user: FuelingEvent) {
    user.isEditing = false;
    this.fetchFuelingEvents(); // ✅ Restore original values
  }

  validateEvent(event: FuelingEvent): string | null {
    if (!event.vehicleNumber.trim()) return "Vehicle number is required.";
    if (!event.date.trim()) return "Date is required.";
    if (!event.fuelCost || event.fuelCost <= 0) return "Fuel cost must be greater than 0.";
    if (!event.fuelAdded || event.fuelAdded <= 0) return "Fuel added is required.";
    if (!event.currentMileage || event.currentMileage <= 0) return "Current Mileage is required.";
    return null;
  }


  saveEvent() {
    if (!this.selectedEvent) return;

    const validationError = this.validateEvent(this.selectedEvent);
    if (validationError) {
      this.showTemporaryMessage('modalErrorMessage', validationError);
      return;
    }

    const apiUrl = `${environment.apiBaseUrl}/api/admin/fueling/add/${this.userId}`;

    this.http.post<{ message?: string; error?: string }>(apiUrl, this.selectedEvent, { headers: this.getHeaders() })
      .subscribe({
        next: (response) => {
          if (response.error) {
            this.showTemporaryMessage('modalErrorMessage', response.error);
            return;
          }
          this.fetchFuelingEvents();
          this.showTemporaryMessage('modalSuccessMessage', response.message || 'Event added successfully!');
          
        },
        error: (errorResponse) => {
          //this.modalErrorMessage = errorResponse?.error?.message || "Failed to add event.";
          this.showTemporaryMessage('modalErrorMessage', errorResponse?.error?.message || "Failed to add event.");
        }
      });
  }

  updateEvent(event: FuelingEvent) {
    if (!event.id) return;

    const validationError = this.validateEvent(event);
    if (validationError) {
      this.showTemporaryMessage('errorMessage', validationError);
      return;
    }

    const apiUrl = `${environment.apiBaseUrl}/api/admin/fueling/update/${event.id}/${this.userId}/${this.isUser}`;

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

  toggleEdit(event: FuelingEvent) {
    if (event.isEditing) {
      this.updateEvent(event);
    } else {
      event.isEditing = true;
    }
  }

  deleteEvent(eventId: number | undefined) {
    if (!eventId) return;
    if (!confirm('Are you sure you want to delete this event?')) return;

    this.http.delete<{ message?: string; error?: string }>(
      `${environment.apiBaseUrl}/api/admin/fueling/delete/${eventId}/${this.userId}/${this.isUser}`, 
      { headers: this.getHeaders() }
    ).subscribe({
      next: (response) => {
        if (response.error) {
          this.showTemporaryMessage('errorMessage', response.error);
          return;
        }
        this.fetchFuelingEvents();
        this.showTemporaryMessage('successMessage', response.message || 'Event deleted successfully!');
      },
      error: (errorResponse) => {
        this.showTemporaryMessage('errorMessage', errorResponse?.error?.message || 'Failed to delete event.');
      }
    });
  }

  openModal() {
    this.selectedEvent = { vehicleNumber: '', date: '', fuelCost: 0, fuelAdded: 0, currentMileage: 0 };
    this.showModal = true;
    this.modalSuccessMessage = '';
    this.modalErrorMessage = '';
  }

  closeModal() {
    this.showModal = false;
    this.selectedEvent = null;
  }

  showTemporaryMessage(type: 'errorMessage' | 'successMessage' | 'modalErrorMessage' | 'modalSuccessMessage', message: string) {
    this[type] = message;
    setTimeout(() => this.clearMessage(type), 3000);
  }

  clearMessage(type: 'errorMessage' | 'successMessage' | 'modalErrorMessage' | 'modalSuccessMessage') {
    this[type] = '';
  }
}
