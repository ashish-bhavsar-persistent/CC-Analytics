import { BrowserModule } from '@angular/platform-browser';
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ChartsModule } from 'ng2-charts';

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
import { MatGridListModule, MatCardModule, MatMenuModule, MatIconModule, MatSlideToggleModule, MatTooltipModule, MatAutocompleteModule, MatButtonToggleModule, MatTabsModule } from '@angular/material';
import { LayoutModule } from '@angular/cdk/layout';
import { AuthGuard } from './guards/auth.guard';
import { TokenInterceptorService } from './services/token-interceptor.service';
import { MatToolbarModule } from '@angular/material/toolbar';
import { ScrollDispatchModule } from '@angular/cdk/scrolling';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { RolesMenuComponent } from './components/roles-menu/roles-menu.component';
import { InformationCardsComponent } from './components/information-cards/information-cards.component';
import { RatePlanDonutChartComponent } from './components/charts/rate-plan-donut-chart/rate-plan-donut-chart.component';
import { CommPlanDonutChartComponent } from './components/charts/comm-plan-donut-chart/comm-plan-donut-chart.component';
import { AccountAnalysisChartComponent } from './components/charts/account-analysis-chart/account-analysis-chart.component';
import { AccountanalysisdataserviceService } from './services/accountanalysisdataservice.service';
import { AcctAnalyticsPiechartComponent } from './components/charts/acct-analytics-piechart/acct-analytics-piechart.component';
import { DeviceStatusComponent } from './components/charts/device-status/device-status.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    MainDashboardComponent,
    NavBarComponent,
    RolesMenuComponent,
    InformationCardsComponent,
    RatePlanDonutChartComponent,
    CommPlanDonutChartComponent,
    AccountAnalysisChartComponent,
    AcctAnalyticsPiechartComponent,
    DeviceStatusComponent
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
    MatSlideToggleModule,
    MatMenuModule,
    MatIconModule,
    MatTooltipModule,
    MatAutocompleteModule,
    ChartsModule,
    ScrollDispatchModule,
    MatButtonToggleModule,
    MatTabsModule
  ],
  providers: [ApiService, AuthService, AuthGuard, AccountanalysisdataserviceService,
     { 
       provide: HTTP_INTERCEPTORS,
       useClass: TokenInterceptorService,
       multi: true
      }
    ],
  schemas:[CUSTOM_ELEMENTS_SCHEMA],
  bootstrap: [AppComponent]
})
export class AppModule { }
