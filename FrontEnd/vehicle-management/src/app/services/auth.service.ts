import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private token: string | null = null;
  private role: string | null = null;
  private refreshToken: string | null = null;
  private firstTimeLogin: boolean = false;
  private logoutTimer: any;

  constructor(private router: Router, private http: HttpClient) {
    // ✅ Load stored credentials if available
    const storedToken = localStorage.getItem('token');
    if (storedToken) {
      this.token = storedToken;
      this.role = localStorage.getItem('role');
      this.refreshToken = localStorage.getItem('refreshToken');
      this.firstTimeLogin = sessionStorage.getItem('firstTimeLogin') === 'true';
    }
  }

  login(userId: string, password: string, rememberMe: boolean): Observable<any> {
    return this.http.post<any>(`${environment.apiBaseUrl}/api/auth/login`, { userId, password }).pipe(
      map(response => {
        if (response.message === 'First-time login. Password change required.') {
          this.firstTimeLogin = true;
          sessionStorage.setItem('firstTimeLogin', 'true');
          this.router.navigate(['/first-time-login']);
          return { success: false };
        }
  
        // ✅ Normal login
        this.token = response.token;
        this.role = response.role.toUpperCase();
        this.refreshToken = response.refreshToken;
        this.firstTimeLogin = false;
  
        this.scheduleAutoLogout(this.getTokenExpiry(response.token));
  
        if (rememberMe) {
          localStorage.setItem('userId', userId);
          localStorage.setItem('token', response.token);
          localStorage.setItem('role', response.role.toUpperCase());
          localStorage.setItem('refreshToken', response.refreshToken);
          localStorage.setItem('firstTimeLogin', 'false');
        } else {
          sessionStorage.setItem('userId', userId);
          sessionStorage.setItem('firstTimeLogin', 'false');
        }
  
        return { success: true };
      })
    );
  }
  

  isFirstTimeLogin(): boolean {
    return sessionStorage.getItem('firstTimeLogin') === 'true';
  }

  isLoggedIn(): boolean {
    return !!this.token;
  }

  getRole(): string | null {
    return this.role;
  }

  logout() {
    this.token = null;
    this.role = null;
    this.refreshToken = null;
    this.firstTimeLogin = false;
    clearTimeout(this.logoutTimer);
    localStorage.removeItem('userId'); // ✅ Clear userId
    sessionStorage.removeItem('userId'); // ✅ Clear userId
    localStorage.clear();
    sessionStorage.clear();
    this.router.navigate(['/']).then(() => window.location.reload());
  }

  private getTokenExpiry(token: string): number {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp ? payload.exp * 1000 : Date.now() + 60000;
    } catch (e) {
      return Date.now() + 60000;
    }
  }

  private scheduleAutoLogout(expiryTime: number) {
    const timeLeft = expiryTime - Date.now();
    if (timeLeft > 0) {
      this.logoutTimer = setTimeout(() => this.showSessionPopup(), timeLeft - 10000);
    } else {
      this.logout();
    }
  }

  private showSessionPopup() {
    if (confirm('Your session is about to expire. Would you like to extend it?')) {
      this.refreshSession();
    } else {
      this.logout();
    }
  }

  refreshSession() {
    if (!this.refreshToken) return;

    this.http.post<any>(`${environment.apiBaseUrl}/api/auth/refresh`, { refreshToken: this.refreshToken }).subscribe({
      next: (response) => {
        this.token = response.token;
        this.refreshToken = response.refreshToken;
        this.scheduleAutoLogout(this.getTokenExpiry(response.token));
        localStorage.setItem('token', response.token);
        localStorage.setItem('refreshToken', response.refreshToken);
      },
      error: () => this.logout()
    });
  }

  getToken(): string | null {
    return this.token;
  }

  getUserId(): string {
    return localStorage.getItem('userId') || sessionStorage.getItem('userId') || '';
  }
  
  
}
