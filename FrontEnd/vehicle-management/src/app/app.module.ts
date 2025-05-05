import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';  
import { AppComponent } from './app.component';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { BaseChartDirective } from 'ng2-charts';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';


// ✅ Import Components Normally (Now Not Standalone)
import { LoginComponent } from './components/login/login.component';
import { AdminComponent } from './components/admin/admin.component';
import { UserComponent } from './components/user/user.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { FirstTimeLoginComponent } from './components/first-time-login/first-time-login.component';
import { ManageUsersComponent } from './components/admin/manage-users/manage-users.component';
import { ManageVehiclesComponent } from './components/admin/manage-vehicles/manage-vehicles.component';
import { AuthService } from './services/auth.service';
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

@NgModule({
  declarations: [
    AppComponent, 
    LoginComponent, 
    AdminComponent, 
    UserComponent, ForgotPasswordComponent, FirstTimeLoginComponent, ManageUsersComponent, ManageVehiclesComponent, ReportsComponent, EventsComponent, MaintenanceEventsComponent, FuelingEventsComponent, ProfileComponent, ChangePasswordComponent, UserProfileComponent, UserEventsComponent, UserFuelingComponent, UserMaintenanceComponent  // ✅ Components are now declared normally
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,  
    RouterModule,
    FormsModule,
    BaseChartDirective,
    HttpClientModule,
    NgbModule  
  ],
  providers: [AuthService],
  bootstrap: [AppComponent]
})
export class AppModule { }
