import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css'],
  standalone: false
})
export class AdminComponent {

  isDropdownOpen: boolean = false;
  dropdownTimer: any;

  constructor(private router: Router, private authService: AuthService) {}

  logout() {
    this.authService.logout();
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
    if (this.isDropdownOpen) {
      clearTimeout(this.dropdownTimer); // Clear any existing timer
      this.dropdownTimer = setTimeout(() => {
        this.isDropdownOpen = false;
      }, 3000); // 3 seconds
    } else {
      clearTimeout(this.dropdownTimer); // If manually closed, clear timer
    }
  } 

  cancelAutoClose() {
    clearTimeout(this.dropdownTimer);
  }
  
  restartAutoClose() {
    this.dropdownTimer = setTimeout(() => {
      this.isDropdownOpen = false;
    }, 3000);
  }
  
}
