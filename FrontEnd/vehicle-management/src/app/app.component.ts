import { Component } from '@angular/core';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.css'
})
export class AppComponent {
  
  constructor(private authService: AuthService) {}

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  isAdmin(): boolean {
    return localStorage.getItem('role') === 'ADMIN';
  }

  isUser(): boolean {
    return localStorage.getItem('role') === 'USER';
  }

  isRootAdmin(): boolean {
    return localStorage.getItem('role') === 'ROOT_ADMIN';
  }

  logout() {
    this.authService.logout();
  }
}