import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { environment } from '../../../../environments/environment';

interface UserProfile {
  userId: string;
  fullName: string;
  email: string;
}

@Component({
  selector: 'app-user-profile',
  standalone: false,
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})
export class UserProfileComponent implements OnInit {
  profile: UserProfile = { userId: '', fullName: '', email: '' };
  successMessage: string = '';
  errorMessage: string = '';
  isEditing: boolean = false; // ✅ Controls edit mode
  speed = 0; // 🔥 Dynamic Speedometer Value

  constructor(private http: HttpClient, private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.profile.userId = this.authService.getUserId(); // ✅ Fetch logged-in user ID
    this.fetchUserProfile();
  }

  getHeaders() {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  }
  
  fetchUserProfile() {
    this.http.get<UserProfile>(`${environment.apiBaseUrl}/api/user/profile/${this.profile.userId}`, { headers: this.getHeaders() })
      .subscribe({
        next: (data) => {
          this.profile.fullName = data.fullName;
          this.profile.email = data.email;
        },
        error: () => {
          this.errorMessage = 'Failed to load profile details.';
          this.hideMessage();
        }
      });
  }

  // ✅ Enable Editing
  enableEdit() {
    this.isEditing = true;
  }

  updateProfile() {
    if (!this.profile.fullName || !this.profile.email) {
      this.errorMessage = 'All fields are required!';
      this.hideMessage();
      return;
    }

    this.http.put(`${environment.apiBaseUrl}/api/user/profile/update/${this.profile.userId}`, this.profile, { headers: this.getHeaders() })
      .subscribe({
        next: (response: any) => {
          if (response.error) {
            this.errorMessage = response.error;
          } else {
            this.successMessage = 'Profile updated successfully!';
            this.isEditing = false; // ✅ Disable editing after update
          }
          this.hideMessage();
        },
        error: (error) => {
          this.errorMessage = error.error?.error || 'Failed to update profile.';
          this.hideMessage();
        }
      });
  }

  navigateToChangePassword() {
    this.router.navigate(['/change-password']);
  }

  hideMessage() {
    setTimeout(() => {
      this.successMessage = '';
      this.errorMessage = '';
    }, 3000);
  }
}
