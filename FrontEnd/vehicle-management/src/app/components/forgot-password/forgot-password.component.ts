import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
  standalone: false
})
export class ForgotPasswordComponent {
  step: number = 1; // ✅ Step counter for form navigation

  userId: string = '';
  securityQuestions: string[] = [];
  securityAnswers: { [key: string]: string } = {}; // ✅ Store Answers Correctly
  newPassword: string = '';
  confirmPassword: string = '';
  isVerified: boolean = false;
  message: string = '';
  errorMessage: string = '';
  countdown: number = 5;

  constructor(private http: HttpClient, private router: Router) {}

  // ✅ Step 1: Fetch Security Questions
  fetchSecurityQuestions() {
    if (!this.userId) {
      this.errorMessage = 'Please enter your User ID.';
      return;
    }

    this.http.post<any>(`${environment.apiBaseUrl}/api/auth/security-questions`, { userId: this.userId }).subscribe({
      next: (response) => {
        this.securityQuestions = Array.isArray(response.questions) ? response.questions : [];
        this.errorMessage = '';
        this.step = 2; // ✅ Move to the next step
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'User ID not found.';
        this.securityQuestions = [];
      }
    });
  }

  // ✅ Step 2: Verify Security Answers
  onVerifyAnswers() {
    if (Object.values(this.securityAnswers).some(answer => !answer.trim())) {
      this.errorMessage = 'Please provide all security answers.';
      return;
    }

    const securityAnswersMap: { [key: string]: string } = {};
    this.securityQuestions.forEach((question) => {
      securityAnswersMap[question] = this.securityAnswers[question]; // ✅ Ensuring correct mapping
    });

    this.http.post<any>(`${environment.apiBaseUrl}/api/auth/verify-security-answers`, {
      userId: this.userId,
      securityAnswers: securityAnswersMap
    }).subscribe({
      next: () => {
        this.isVerified = true;
        this.errorMessage = '';
        this.step = 3; // ✅ Move to password reset step
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Security answers do not match.';
        this.isVerified = false;
      }
    });
  }

  // ✅ Step 3: Reset Password
  onResetPassword() {
    if (!this.newPassword || !this.confirmPassword) {
      this.errorMessage = 'Both password fields are required.';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    const securityAnswersMap: { [key: string]: string } = {};
    this.securityQuestions.forEach((question) => {
      securityAnswersMap[question] = this.securityAnswers[question]; // ✅ Correct mapping
    });

    const payload = {
      userId: this.userId,
      securityAnswers: securityAnswersMap,
      newPassword: this.newPassword,
      confirmPassword: this.confirmPassword
    };

    this.http.put<any>(`${environment.apiBaseUrl}/api/auth/forgot-password`, payload).subscribe({
      next: (response) => {
        this.message = response.message + ` Redirecting to login in ${this.countdown} seconds...`;
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
        this.errorMessage = err.error?.message || 'Failed to reset password.';
        this.message = '';
      }
    });
  }
}
