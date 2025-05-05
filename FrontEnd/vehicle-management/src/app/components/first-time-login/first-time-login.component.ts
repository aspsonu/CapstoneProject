import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-first-time-login',
  templateUrl: './first-time-login.component.html',
  styleUrls: ['./first-time-login.component.css'],
  standalone: false
})
export class FirstTimeLoginComponent {
  userId: string = '';
  currentPassword: string = '';
  newPassword: string = '';
  confirmPassword: string = '';

  securityQuestion1: string = '';
  securityQuestion2: string = '';
  securityAnswer1: string = '';
  securityAnswer2: string = '';

  message: string = '';
  errorMessage: string = '';
  countdown: number = 5;

  step: number = 1; // ✅ Step indicator
  formCompleted: boolean = false; // ✅ Hide form after success

  constructor(private http: HttpClient, private router: Router) {}

  nextStep() {
    this.errorMessage = '';
  
    if (this.step === 1) {
      if (!this.userId || !this.currentPassword) {
        this.errorMessage = 'User ID and Current Password are required.';
        setTimeout(() => this.errorMessage = '', 3000);
        return;
      }
  
      this.http.post<any>(`${environment.apiBaseUrl}/api/auth/login`, {
        userId: this.userId,
        password: this.currentPassword
      }).subscribe({
        next: (response) => {
          if (response.firstTimeLogin) {
            this.step++;
          } else {
            this.errorMessage = 'This is not a first-time login.';
            setTimeout(() => this.errorMessage = '', 3000);
          }
        },
        error: (err) => {
          this.errorMessage = err.error?.error || 'Invalid User ID or Password.';
          setTimeout(() => this.errorMessage = '', 3000);
        }
      });
  
      return;
    }
  
    if (this.step === 2) {
      if (!this.newPassword || !this.confirmPassword) {
        this.errorMessage = 'Please enter and confirm your new password.';
        setTimeout(() => this.errorMessage = '', 3000);
        return;
      }
  
      if (this.newPassword !== this.confirmPassword) {
        this.errorMessage = 'New Password and Confirm Password do not match.';
        setTimeout(() => this.errorMessage = '', 3000);
        return;
      }
  
      this.step++;
      return;
    }
  
    if (this.step === 3) {
      if (
        !this.securityQuestion1.trim() || !this.securityAnswer1.trim() ||
        !this.securityQuestion2.trim() || !this.securityAnswer2.trim()
      ) {
        this.errorMessage = 'Please provide both security questions and their answers.';
        setTimeout(() => this.errorMessage = '', 3000);
        return;
      }
  
      // ✅ All validations passed → Submit now
      this.onFirstTimeLogin();
    }
  }
  

  onFirstTimeLogin() {
    const securityQuestions = {
      [this.securityQuestion1]: this.securityAnswer1,
      [this.securityQuestion2]: this.securityAnswer2
    };

    const payload = {
      userId: this.userId,
      currentPassword: this.currentPassword,
      newPassword: this.newPassword,
      confirmPassword: this.confirmPassword,
      securityQuestions
    };

    this.http.post<any>(`${environment.apiBaseUrl}/api/auth/first-time-login`, payload).subscribe({
      next: (response) => {
        this.message = response.message + ` Redirecting to login in ${this.countdown} seconds...`;
        sessionStorage.setItem('firstTimeLogin', 'false');
        this.formCompleted = true; // ✅ Hide form after success

        const interval = setInterval(() => {
          this.countdown--;
          this.message = response.message + ` Redirecting to login in ${this.countdown} seconds...`;
          if (this.countdown <= 0) {
            clearInterval(interval);
            this.router.navigate(['/']);
          }
        }, 1000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'First-time login failed.';
        this.message = '';
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }
}
