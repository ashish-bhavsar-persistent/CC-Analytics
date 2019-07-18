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
import { MatBottomSheetModule } from '@angular/material/bottom-sheet';
import { MatDividerModule } from '@angular/material/divider';
import { MatSelectModule } from '@angular/material/select';
import {MatDialogModule} from '@angular/material/dialog';
import { AlertModule } from 'ngx-bootstrap';
import { TypeaheadModule } from 'ngx-bootstrap';
import { ButtonsModule } from 'ngx-bootstrap';
import { ModalModule } from 'ngx-bootstrap';


import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { RolesMenuComponent } from './components/roles-menu/roles-menu.component';
import { InformationCardsComponent } from './components/information-cards/information-cards.component';
import { RatePlanDonutChartComponent } from './components/charts/rate-plan-donut-chart/rate-plan-donut-chart.component';
import { CommPlanDonutChartComponent } from './components/charts/comm-plan-donut-chart/comm-plan-donut-chart.component';
import { AccountanalysisdataserviceService } from './services/accountanalysisdataservice.service';
import { AcctAnalyticsPiechartComponent } from './components/charts/acct-analytics-piechart/acct-analytics-piechart.component';
import { LoaderComponent } from './components/loader/loader.component';
import { AccountBarChartComponent } from './components/charts/account-bar-chart/account-bar-chart.component';
import { BottomSheetComponent } from './components/bottom-sheet/bottom-sheet.component';
import { MainDashboardSysadminComponent } from './components/main-dashboard-sysadmin/main-dashboard-sysadmin.component';
import { SpadminDashboardComponent } from './spadmin-dashboard/spadmin-dashboard.component';
import { UserDashboardComponent } from './user-dashboard/user-dashboard.component';
import { ModalComponent } from './components/modal/modal.component';


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
    AcctAnalyticsPiechartComponent,
    LoaderComponent,
    AccountBarChartComponent,
    BottomSheetComponent,
    MainDashboardSysadminComponent,
    SpadminDashboardComponent,
    UserDashboardComponent,
    ModalComponent
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
    MatTabsModule,
    MatBottomSheetModule,
    MatDividerModule,
    MatDialogModule,
    MatSelectModule,
    MatMenuModule,
    AlertModule.forRoot(),
    TypeaheadModule.forRoot(),
    ButtonsModule.forRoot(),
    ModalModule.forRoot()
  ],
  providers: [ApiService, AuthService, AuthGuard, AccountanalysisdataserviceService,
     { 
       provide: HTTP_INTERCEPTORS,
       useClass: TokenInterceptorService,
       multi: true
      }
    ],
    entryComponents: [BottomSheetComponent, ModalComponent],
  schemas:[CUSTOM_ELEMENTS_SCHEMA],
  bootstrap: [AppComponent]
})

export class AppModule { }
