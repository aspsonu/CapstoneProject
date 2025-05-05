import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/']);
      return false;
    }

    const expectedRoles: string[] = route.data['roles'] || [];
    const userRole = this.authService.getRole();

    if (expectedRoles.length > 0 && !expectedRoles.includes(userRole || '')) {
      this.router.navigate(['/']); // Redirect to unauthorized page
      return false;
    }

    /** const expectedRole = route.data['role'];
    if (expectedRole && this.authService.getRole() !== expectedRole) {
      this.router.navigate(['/']);
      return false;
    } **/

    return true;
  }
}
