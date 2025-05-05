import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: false
})
export class LoginComponent {
  userId: string = '';
  password: string = '';
  rememberMe: boolean = false;
  errorMessage: string = '';
  showPassword: boolean = false;

  constructor(public authService: AuthService, private router: Router) {}

  onLogin() {
    if (!this.userId || !this.password) {
      this.errorMessage = 'Please enter valid credentials.';
      return;
    }
  
    this.authService.login(this.userId, this.password, this.rememberMe).subscribe({
      next: (response) => {
        if (response.success === false && this.authService.isFirstTimeLogin()) {
          // Already navigated to first-time login
          return;
        }
  
        if (response.success === true) {
          const role = this.authService.getRole();
          if (role === 'ADMIN' || role === 'ROOT_ADMIN') {
            this.router.navigate(['/admin']);
          } else {
            this.router.navigate(['/user']);
          }
        }
      },
      error: (err) => {
        const msg = err?.error?.message || 'Login failed. Please try again.';
        if (msg.includes('disabled') || msg.includes('account')) {
          this.errorMessage = 'Your account has been disabled. Please contact the administrator.';
        } else {
          this.errorMessage = msg;
        }
      }
    });
  }
  

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }
}
