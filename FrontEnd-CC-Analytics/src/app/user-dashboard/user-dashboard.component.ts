import { Component, OnInit, ViewChild } from "@angular/core";
import { map, distinctUntilChanged, debounceTime, merge, filter } from "rxjs/operators";
import { Breakpoints, BreakpointObserver } from "@angular/cdk/layout";
import { NavBarComponent } from "../components/nav-bar/nav-bar.component";
import { AuthService } from "../services/auth.service";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { environment } from "../../environments/environment.prod";
import { Observable, Subject } from "rxjs";
import { timer } from "rxjs";
import { AccountanalysisdataserviceService } from "../services/accountanalysisdataservice.service";
import { ChartDataSets, ChartOptions, ChartType } from "chart.js";
import { Color, BaseChartDirective, Label } from "ng2-charts";
import {
  MatBottomSheet,
  MatBottomSheetRef
} from "@angular/material/bottom-sheet";
import { BottomSheetComponent } from "../components/bottom-sheet/bottom-sheet.component";
import { FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'user-dashboard',
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.scss']
})
export class UserDashboardComponent implements OnInit {

  private username: string = "";
  private roles: any;
  private name: string;
  private today = Date.now();
  private lastUpdatedTime = Date.now();
  private placeholderData = "Accounts";
  private deviceStatus: any = [];
  private dataLoading: boolean = true;
  private deviceDataUnavailable: boolean = true;
  private deviceDataAvailable: boolean = false;
  private topThreeRatePlanValues: any = [];
  private topThreeCommPlanValues: any = [];

  private accountData: any = [];

  private deviceRatePlan: any = [];
  private deviceCommPlan: any = [];

  private spAdminList: any = [];

  private selectedSpAdminId: string = "";

  private spAdminDataLoading: boolean = true;
  private dataLoaded: boolean = false;
  private loadSysadmin: boolean = false;
  private loadSpadmin: boolean = false;

  private accountParameter = {
    adminId: "",
    accountId: "",
    granularity: ""
  }

  stateCtrl = new FormControl();
 
  myForm = new FormGroup({
    state: this.stateCtrl
  });
 
  private accounts: string[] = [];



  constructor(
    private breakpointObserver: BreakpointObserver,
    private _authService: AuthService,
    private http: HttpClient,
    private accAnalysisService: AccountanalysisdataserviceService,
    private _bottomSheet: MatBottomSheet
  ) {}


  

  ngOnInit() {

    Observable;
    timer(1, 1000).subscribe(() => {
      this.today = Date.now();
    });

    this.lastUpdatedTime = Date.now();

    
    let headers = new HttpHeaders({
      Authorization: "Bearer " + `${sessionStorage.getItem("token")}`
    });

    this.http.get(environment.ENV.baseURL+'/api/v1/devices/ratePlan', {headers:headers})
    .subscribe(res => {
      let deviceRateData:any = res;
      this.deviceRatePlan = deviceRateData;
    })

    this.http.get(environment.ENV.baseURL+'/api/v1/devices/commPlan', {headers:headers})
    .subscribe(res => {
      let deviceCommData:any = res;
      this.deviceCommPlan = deviceCommData;
    })
    
    this.http //Fetching User Roles and Details
      .get(environment.ENV.baseURL + "/api/v1/users/me", { headers: headers })
      .subscribe(res => {
        let userInfo: any = res;
        this.username = userInfo.username;
        this.roles = userInfo.roles;
        this.name = userInfo.name;
      });

    this.http //Fetching SP Admin List
      .get(environment.ENV.baseURL + "/api/v1/admins/name", {
        headers: headers
      })
      .subscribe(res => {
        let fetchedData: any = res;
        this.selectedSpAdminId = fetchedData[0].id;
        this.accountParameter.adminId = this.selectedSpAdminId;
        for (let i of fetchedData) {
          this.spAdminList.push(i);
        }
      });

      this.http //Fetching account details based on account ID
      .get(
        environment.ENV.baseURL +
          "/api/v1/accounts/name",
        {
          headers: headers
        }
      )
      .subscribe(res => {
        this.accounts = [];
        let fetchedAccountNames: any = res;
        for(let account of fetchedAccountNames){
          this.accounts.push(account.accountName +' : '+ account.accountId);
        }
      });

      this.http.get(environment.ENV.baseURL+'/api/v1/devices/status', {headers:headers})
    .subscribe(res => {
      this.accountData = res;
      }
    )

  }

  ngOnDestroy(): void {
    this._authService.logout();
  }
}
