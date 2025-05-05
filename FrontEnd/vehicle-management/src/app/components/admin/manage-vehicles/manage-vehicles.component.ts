import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../../services/auth.service';
import { environment } from '../../../../environments/environment';

interface Vehicle {
  vehicleId: number;
  vehicleNumber: string;
  vehicleIdentificationNumber: string;
  modelYear: number;
  make: string;
  model: string;
  purchaseDate: string;
  startingMileage: number;
  vehicleWeight: string;
  vehicleType: string;
  vehicleDescription: string;
  lawEnforcement: boolean;
  exemptType: boolean;  // ✅ Backend sends 1 (Yes) or 0 (No)
  isEditing?: boolean;
  deleted?: boolean;
}

@Component({
  selector: 'app-manage-vehicles',
  templateUrl: './manage-vehicles.component.html',
  styleUrls: ['./manage-vehicles.component.css'],
  standalone: false
})
export class ManageVehiclesComponent implements OnInit {
  vehicles: Vehicle[] = [];
  statusFilter: 'all' | 'active' | 'deleted' = 'all';
  filteredVehicles: Vehicle[] = [];
  newVehicle: Partial<Vehicle> = {
    vehicleNumber: '',
    vehicleIdentificationNumber: '',
    modelYear: undefined,
    make: '',
    model: '',
    purchaseDate: '',
    startingMileage: undefined,
    vehicleWeight: '',
    vehicleType: '',
    vehicleDescription: '',
    lawEnforcement: false
  };
  showModal: boolean = false;
  message: string = '';  
  errorMessage: string = '';  
  modalMessage: string = '';  
  modalErrorMessage: string = '';  
  searchQuery: string = '';

  constructor(private http: HttpClient, private authService: AuthService) {}

  @ViewChild('modalContent') modalContentRef!: ElementRef;

  ngOnInit(): void {
    this.fetchVehicles();
  }

  // ✅ Fetch Vehicles from Backend
  fetchVehicles() {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });

    this.http.get<Vehicle[]>(`${environment.apiBaseUrl}/api/admin/vehicle/list`, { headers }).subscribe({
      next: (data) => {
        this.vehicles = data.map(vehicle => ({
          ...vehicle,
          isEditing: false
        }));
        this.applyFilter();
        this.errorMessage = '';
      },
      error: (err) => {
        this.errorMessage = err.error?.error || 'Failed to load vehicles.';
      }
    });
  }

  /*applyFilter() {
    if (this.statusFilter === 'active') {
      this.filteredVehicles = this.vehicles.filter(v => !v.deleted);
    } else if (this.statusFilter === 'deleted') {
      this.filteredVehicles = this.vehicles.filter(v => v.deleted);
    } else {
      this.filteredVehicles = [...this.vehicles];
    }
  } */

  applyFilter() {
    let filteredVehicles = [...this.vehicles];
    
    if (this.statusFilter === 'active') {
      filteredVehicles = filteredVehicles.filter(v => !v.deleted);
    } else if (this.statusFilter === 'deleted') {
      filteredVehicles = filteredVehicles.filter(v => v.deleted);
    }
  
    if (this.searchQuery.trim()) {
      const q = this.searchQuery.trim().toLowerCase();
      filteredVehicles = filteredVehicles.filter(v => v.vehicleNumber.toLowerCase().includes(q));
    }
  
    this.filteredVehicles = filteredVehicles;
  }
  

  // ✅ Show Add Vehicle Modal
  openModal() {
    this.showModal = true;
    this.modalMessage = '';
    this.modalErrorMessage = '';
    this.newVehicle = {
      vehicleNumber: '',
      vehicleIdentificationNumber: '',
      modelYear: undefined,
      make: '',
      model: '',
      purchaseDate: '',
      startingMileage: undefined,
      vehicleWeight: '',
      vehicleType: '',
      vehicleDescription: '',
      lawEnforcement: false
    };
  }

  // ✅ Close Modal & Reset Form
  closeModal() {
    this.showModal = false;
    this.modalMessage = '';
    this.modalErrorMessage = '';
  }
  

  // ✅ Add Vehicle
  addVehicle() {
    if (!this.newVehicle.vehicleNumber || !this.newVehicle.vehicleIdentificationNumber || 
        !this.newVehicle.modelYear || !this.newVehicle.make || !this.newVehicle.model ||
        !this.newVehicle.purchaseDate || !this.newVehicle.startingMileage ||
        !this.newVehicle.vehicleWeight || !this.newVehicle.vehicleType || 
        !this.newVehicle.vehicleDescription) {
      this.modalErrorMessage = 'All fields are required!';
      this.scrollModalToTop();
      return;
    }

    const payload = { ...this.newVehicle };

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });

    this.http.post<any>(`${environment.apiBaseUrl}/api/admin/vehicle/add`, payload, { headers }).subscribe({
      next: (response) => {
        if (response.error) {
          this.modalErrorMessage = response.error;
          this.modalMessage = '';
        } else {
          this.fetchVehicles();
          this.modalMessage = response.message || 'Vehicle added successfully!';
          this.modalErrorMessage = '';
          this.scrollModalToTop();
          setTimeout(() => this.closeModal(), 2000);
        }
      },
      error: (error) => {
        this.modalErrorMessage = error.error?.error || 'Failed to add vehicle.';
        this.modalMessage = '';
      }
    });
  }

  enableEdit(vehicle: Vehicle) {
    vehicle.isEditing = true;
  }
  
  cancelEdit(vehicle: Vehicle) {
    vehicle.isEditing = false;
    this.fetchVehicles();
  }
  
  
  saveVehicle(vehicle: Vehicle) {
    if (!vehicle.vehicleIdentificationNumber) {
      this.errorMessage = 'Vehicle Identification Number is missing! Cannot update.';
      return;
    }
    
    const payload = { ...vehicle };
  
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  
    this.http.put<any>(`${environment.apiBaseUrl}/api/admin/vehicle/update/${vehicle.vehicleId}`, payload, { headers }).subscribe({
      next: (response) => {
        if (response.error) {
          this.errorMessage = response.error;
          this.message = '';
        } else {
          this.fetchVehicles();
          vehicle.isEditing = false;
          this.message = response.message || 'Vehicle updated successfully!';
          this.errorMessage = '';
          setTimeout(() => this.message = '', 3000); 
        }
      },
      error: (error) => {
        this.errorMessage = error.error?.error || 'Failed to update vehicle.';
        this.message = '';
        setTimeout(() => this.message = '', 3000); 
      }
    });
  }
  

  // ✅ Delete Vehicle
  confirmDeleteVehicle(vehicleId: number) {
    if (confirm("Are you sure you want to delete this vehicle?")) {
      this.deleteVehicle(vehicleId);
    }
  }

  deleteVehicle(vehicleId: number) {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  
    this.http.delete<any>(`${environment.apiBaseUrl}/api/admin/vehicle/delete/${vehicleId}`, { headers }).subscribe({
      next: (response) => {
        if (response.error) {
          this.errorMessage = response.error;
          this.message = '';
        } else {
          this.fetchVehicles();
          this.message = response.message || 'Vehicle deleted successfully!';
          this.errorMessage = '';
          setTimeout(() => this.message = '', 3000); 
        }
      },
      error: (error) => {
        this.errorMessage = error.error?.error || 'Failed to delete vehicle.';
        this.message = '';
      }
    });
  }

  scrollModalToTop(): void {
    if (this.modalContentRef && this.modalContentRef.nativeElement) {
      this.modalContentRef.nativeElement.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  reactivateVehicle(vehicleId: number) {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  
    if (confirm("Are you sure you want to reactivate this vehicle?")) {
      this.http.put<any>(`${environment.apiBaseUrl}/api/admin/vehicle/reactivate/${vehicleId}`, {}, { headers }).subscribe({
        next: (response) => {
          this.message = response.message || 'Vehicle reactivated successfully!';
          this.errorMessage = '';
          this.fetchVehicles();
          setTimeout(() => this.message = '', 3000);
        },
        error: (error) => {
          this.errorMessage = error.error?.error || 'Failed to reactivate vehicle.';
          this.message = '';
          setTimeout(() => this.errorMessage = '', 3000);
        }
      });
    }
  }
  
  
}
