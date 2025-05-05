import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../../services/auth.service';
import { environment } from '../../../../environments/environment';

interface User {
  userId: string;
  fullName: string;
  email: string;
  role: string;
  isEditing?: boolean; 
  originalData?: Partial<User>; // Store original data before editing
  deleted?: boolean; 
}

@Component({
  selector: 'app-manage-users',
  templateUrl: './manage-users.component.html',
  styleUrls: ['./manage-users.component.css'],
  standalone: false
})
export class ManageUsersComponent implements OnInit {
  users: User[] = [];
  newUser = { userId: '', fullName: '', email: '', password: '', role: '' };
  showModal: boolean = false;
  message: string = '';
  errorMessage: string = '';
  modalMessage: string = '';
  modalErrorMessage: string = '';

  statusFilter: 'all' | 'active' | 'deleted' = 'all';
  filteredUsers: User[] = [];

  searchQuery: string = '';

  constructor(private http: HttpClient, private authService: AuthService) {}

  ngOnInit(): void {
    this.fetchUsers();
  }

  //Fetch Users from Backend
  fetchUsers() {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });

    this.http.get<User[]>(`${environment.apiBaseUrl}/api/admin/users`, { headers }).subscribe({
      next: (data) => {
        this.users = data.map(user => ({
          ...user,
          isEditing: false,
          originalData: { ...user }
        }));
        this.applyFilter();
        this.errorMessage = '';
        setTimeout(() => this.errorMessage = '', 3000);
      },
      error: (err) => {
        this.errorMessage = err.error?.error || 'Failed to load users.';
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  /*applyFilter() {
    if (this.statusFilter === 'active') {
      this.filteredUsers = this.users.filter(user => !user.deleted);
    } else if (this.statusFilter === 'deleted') {
      this.filteredUsers = this.users.filter(user => user.deleted);
    } else {
      this.filteredUsers = [...this.users]; // All
    }
  } */
  
  applyFilter() {
    let tempUsers = [...this.users];
    
    if (this.statusFilter === 'active') {
      tempUsers = tempUsers.filter(user => !user.deleted);
    } else if (this.statusFilter === 'deleted') {
      tempUsers = tempUsers.filter(user => user.deleted);
    }
  
    if (this.searchQuery.trim()) {
      const q = this.searchQuery.trim().toLowerCase();
      tempUsers = tempUsers.filter(user => user.fullName.toLowerCase().includes(q));
    }
  
    this.filteredUsers = tempUsers;
  }
  

  // ✅ Show Add User Modal
  openModal() {
    this.showModal = true;
    this.modalMessage = ''; // ✅ Reset modal messages
    this.modalErrorMessage = '';
    this.newUser = { userId: '', fullName: '', email: '', password: '', role: '' };
  }

  // ✅ Close Modal & Reset Form
  closeModal() {
    this.showModal = false;
    this.modalMessage = '';
    this.modalErrorMessage = '';
  }

  // ✅ Add User
  addUser() {
    if (!this.newUser.userId || !this.newUser.fullName || !this.newUser.email || !this.newUser.password || !this.newUser.role) {
      this.modalErrorMessage = 'All fields are required!';
      setTimeout(() => this.modalErrorMessage = '', 3000);
      return;
    }

    const payload = {
      userId: this.newUser.userId.trim(),
      fullName: this.newUser.fullName.trim(),
      email: this.newUser.email.trim(),
      password: this.newUser.password.trim(),
      role: this.newUser.role.toUpperCase(),
    };

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });

    this.http.post<any>(`${environment.apiBaseUrl}/api/admin/create-user`, payload, { headers }).subscribe({
      next: (response) => {
        if (response.error) {
          this.modalErrorMessage = response.error;
          this.modalMessage = '';
        } else {
          this.fetchUsers();
          this.modalMessage = response.message || 'User created successfully!';
          this.modalErrorMessage = '';
          setTimeout(() => this.closeModal(), 2000); // ✅ Auto-close modal after success
        }
      },
      error: (error) => {
        this.modalErrorMessage = error.error?.error || 'Failed to add user.';
        this.modalMessage = '';
      }
    });
  }

  // ✅ Enable Inline Editing for a User
  enableEdit(user: User) {
    user.isEditing = true;
  }

  // ✅ Save Updated User Details
  saveUser(user: User) {
    const payload = {
      fullName: user.fullName.trim(),
      email: user.email.trim(),
      role: user.role.toUpperCase(),
    };

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });

    this.http.put<any>(`${environment.apiBaseUrl}/api/admin/update-user/${user.userId}`, payload, { headers }).subscribe({
      next: (response) => {
        if (response.error) {
          this.errorMessage = response.error;
          this.message = '';
        } else {
          user.isEditing = false;
          this.message = response.message || 'User updated successfully!';
          this.errorMessage = '';
          setTimeout(() => this.message = '', 3000); 
        }
      },
      error: (error) => {
        this.errorMessage = error.error?.error || 'Failed to update user.';
        this.message = '';
        setTimeout(() => this.errorMessage = '', 3000); 
      }
    });
  }

  // ✅ Cancel Editing Mode
  cancelEdit(user: User) {
    user.isEditing = false;
    this.fetchUsers(); // ✅ Restore original values
  }

  // ✅ Delete User
  confirmDeleteUser(userId: string) {
    if (confirm("Are you sure you want to delete this user?")) {
      this.deleteUser(userId);
    }
  }

  deleteUser(userId: string) {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });

    this.http.delete<any>(`${environment.apiBaseUrl}/api/admin/delete-user/${userId}`, { headers }).subscribe({
      next: (response) => {
        if (response.error) {
          this.errorMessage = response.error;
          this.message = '';
          setTimeout(() => this.errorMessage = '', 3000);
        } else {
          this.fetchUsers();
          this.message = response.message || 'User deleted successfully!';
          this.errorMessage = '';
          setTimeout(() => this.message = '', 3000);
        }
      },
      error: (error) => {
        this.errorMessage = error.error?.error || 'Failed to delete user.';
        this.message = '';
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  reactivateUser(userId: string) {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  
    if (confirm("Are you sure you want to reactivate this user?")) {
      this.http.put<any>(`${environment.apiBaseUrl}/api/admin/reactivate-user/${userId}`, {}, { headers }).subscribe({
        next: (response) => {
          if (response.error) {
            this.errorMessage = response.error;
            this.message = '';
          } else {
            this.message = response.message || 'User reactivated successfully!';
            this.errorMessage = '';
            this.fetchUsers(); // ✅ Refresh user list
            setTimeout(() => this.message = '', 3000);
          }
        },
        error: (error) => {
          this.errorMessage = error.error?.error || 'Failed to reactivate user.';
          this.message = '';
          setTimeout(() => this.errorMessage = '', 3000);
        }
      });
    }
  }

  searchUsers() {
    const lowerQuery = this.searchQuery.toLowerCase();
    this.filteredUsers = this.users.filter(user =>
      user.fullName.toLowerCase().includes(lowerQuery)
    );
  }
  
}
