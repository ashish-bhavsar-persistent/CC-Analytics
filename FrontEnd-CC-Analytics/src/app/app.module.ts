import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ApiService } from './services/api.service';
import { HomeComponent } from './components/home/home.component';
import { AuthService } from './services/auth.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatInputModule } from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule} from '@angular/material/button';
import { MainDashboardComponent } from './main-dashboard/main-dashboard.component';
import { MatGridListModule, MatCardModule, MatMenuModule, MatIconModule, MatSlideToggleModule } from '@angular/material';
import { LayoutModule } from '@angular/cdk/layout';
import { AuthGuard } from './guards/auth.guard';
import { TokenInterceptorService } from './services/token-interceptor.service';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    MainDashboardComponent,
    NavBarComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatInputModule,
    MatFormFieldModule,
    MatButtonModule,
    AppRoutingModule,
    MatGridListModule,
    MatCardModule,
    MatMenuModule,
    MatIconModule,
    LayoutModule,
    MatToolbarModule,
    MatSlideToggleModule
  ],
  providers: [ApiService, AuthService, AuthGuard,
     { 
       provide: HTTP_INTERCEPTORS,
       useClass: TokenInterceptorService,
       multi: true
      }
    ],
  bootstrap: [AppComponent]
})
export class AppModule { }
