import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css',
  standalone: false
})
export class ChangePasswordComponent {
  oldPassword: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  successMessage: string = '';
  errorMessage: string = '';
  userRole: string = '';

  constructor(private http: HttpClient, private authService: AuthService, private router: Router) {
    this.userRole = this.authService.getRole() ?? '';
  }

  getHeaders() {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  }

  changePassword() {
    if (!this.oldPassword || !this.newPassword || !this.confirmPassword) {
      this.errorMessage = 'All fields are required!';
      this.hideMessage();
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'New passwords do not match!';
      this.hideMessage();
      return;
    }

    const userId = this.authService.getUserId();
    const payload = {
      currentPassword: this.oldPassword,
      newPassword: this.newPassword,
      confirmPassword: this.confirmPassword
    };

    this.http.put(`${environment.apiBaseUrl}/api/user/profile/change-password/${userId}`, payload, { headers: this.getHeaders() })
      .subscribe({
        next: (response: any) => {
          if (response.error) {
            this.errorMessage = response.error;
          } else {
            this.successMessage = 'Password updated successfully!';
            this.oldPassword = '';
            this.newPassword = '';
            this.confirmPassword = '';
            // Redirect back to profile page after 2 seconds
            setTimeout(() => {
              const redirectPath = this.userRole.toUpperCase() === 'ADMIN' ? '/admin/profile' : '/user/user-profile';
              this.router.navigate([redirectPath]);
            }, 2000);
            }
            this.hideMessage();
        },
        error: (error) => {
          this.errorMessage = error.error?.error || 'Failed to update password.';
          this.hideMessage();
        }
      });
  }

  hideMessage() {
    setTimeout(() => {
      this.successMessage = '';
      this.errorMessage = '';
    }, 3000);
  }
}
