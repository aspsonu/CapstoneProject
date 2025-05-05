import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { AdminComponent } from './components/admin/admin.component';
import { UserComponent } from './components/user/user.component';
import { AuthGuard } from './guards/auth.guard';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { FirstTimeLoginComponent } from './components/first-time-login/first-time-login.component';
import { ManageUsersComponent } from './components/admin/manage-users/manage-users.component';
import { ManageVehiclesComponent } from './components/admin/manage-vehicles/manage-vehicles.component';
import { ReportsComponent } from './components/admin/reports/reports.component';
import { EventsComponent } from './components/admin/events/events.component';
import { MaintenanceEventsComponent } from './components/admin/events/maintenance-events/maintenance-events.component';
import { FuelingEventsComponent } from './components/admin/events/fueling-events/fueling-events.component';
import { ProfileComponent } from './components/admin/profile/profile.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { UserProfileComponent } from './components/user/user-profile/user-profile.component';
import { UserEventsComponent } from './components/user/user-events/user-events.component';
import { UserFuelingComponent } from './components/user/user-events/user-fueling/user-fueling.component';
import { UserMaintenanceComponent } from './components/user/user-events/user-maintenance/user-maintenance.component';

const routes: Routes = [
  { path: '', component: LoginComponent },  // ✅ Default route is login page
  { path: 'admin', component: AdminComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'ROOT_ADMIN'] } ,
    children: [
      { path: 'profile', component: ProfileComponent },
      { path: 'manage-users', component: ManageUsersComponent },
      { path: 'manage-vehicles', component: ManageVehiclesComponent },
      { path: 'reports', component: ReportsComponent },
      { path: 'events', component: EventsComponent},
      { path: 'events/maintenance', component: MaintenanceEventsComponent },
      { path: 'events/fueling', component: FuelingEventsComponent },
      { path: '', redirectTo: 'profile', pathMatch: 'full' }  // Default child route
    ]
  },
  { path: 'user', component: UserComponent, canActivate: [AuthGuard], data: { role: 'USER' }, 
    children: [
      { path: 'user-profile', component: UserProfileComponent},
      { path: 'user-events', component: UserEventsComponent},
      { path: 'user-events/user-fueling', component: UserFuelingComponent},
      { path: 'user-events/user-maintenance', component: UserMaintenanceComponent},
      { path: '', redirectTo: 'user-profile', pathMatch: 'full'}
    ]
  },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'first-time-login', component: FirstTimeLoginComponent },
  { path: 'change-password', component: ChangePasswordComponent},
  { path: '**', redirectTo: '' } // Redirect unknown routes to login
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
