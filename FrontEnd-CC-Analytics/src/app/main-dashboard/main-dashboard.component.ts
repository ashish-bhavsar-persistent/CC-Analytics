import { Component, OnInit } from "@angular/core";
import { map } from "rxjs/operators";
import { Breakpoints, BreakpointObserver } from "@angular/cdk/layout";
import { NavBarComponent } from "../components/nav-bar/nav-bar.component";
import { AuthService } from "../services/auth.service";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { environment } from "../../environments/environment.prod";
import { Observable } from "rxjs";
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
  selector: "app-main-dashboard",
  templateUrl: "./main-dashboard.component.html",
  styleUrls: ["./main-dashboard.component.scss"]
})
export class MainDashboardComponent implements OnInit {
  private username: string = "";
  private roles: any;
  private name: string;
  private today = Date.now();
  private lastUpdatedTime = Date.now();
  private placeholderData = "Accounts";
  private deviceStatus: any = [];
  private dataLoading: boolean = true;
  private topThreeRatePlanValues: any = [];
  private topThreeCommPlanValues: any = [];

  private spAdminList: any = [];

  private selectedSpAdminId: string = "";

  private spAdminDataLoading: boolean = true;
  private dataLoaded: boolean = false;

  stateCtrl = new FormControl();
 
  myForm = new FormGroup({
    state: this.stateCtrl
  });
 
  accounts = [];

  //Rate Plan Chart Variables//

  public ratePlanChartOptions: ChartOptions = {
    responsive: true,
    legend: {
      display: false,
      position: "top"
    },
    elements: {
      arc: {
          borderWidth: 0
      }
  }
  };
  public ratePlanChartLabels = [];
  public ratePlanChartData = [];
  public ratePlanChartType: ChartType = "pie";
  public ratePlanChartLegend = true;
  public ratePlanChartColors = [
    {
    backgroundColor:[  
      "#4a498f",
      "#6b569d",
      "#8b64aa",
      "#a973b7",
      "#c683c2",
      "#e394ce",
      "#ffa6d9",
      "#ff8dc0",
      "#ff74a3",
      "#ff5982",
      "#fc3e5d",
      "#f02136",
      "#de0000"]
    }
  ];
  //Rate Plan Chart Variables//

  //Comm Plan Chart Variables//

  public commPlanChartOptions: ChartOptions = {
    responsive: true,
    elements: {
      arc: {
          borderWidth: 0
      }
  },
    legend: {
      display: false,
      position: "top"
    },
    plugins: {
      datalabels: {
        formatter: (value, ctx) => {
          const label = ctx.chart.data.labels[ctx.dataIndex];
          return label;
        }
      }
    }
  };
  public commPlanChartLabels = [];
  public commPlanChartData = [];
  public commPlanChartType: ChartType = "pie";
  public commPlanChartLegend = true;
  public commPlanChartColors = [
    {
      backgroundColor: [
        "#278f83",
        "#2ea196",
        "#35b3aa",
        "#3cc6be",
        "#43d8d3",
        "#4bece9",
        "#52ffff",
        "#00ecff",
        "#00d7ff",
        "#00c3fe",
        "#00adf8",
        "#0096ed",
        "#107fde"
      ]
    }
  ];

  //Comm Plan Chart Variables//

  //Device Status Chart Variables
  public lineChartData = [
    { data: [65, 59, 80, 81, 56, 55, 40], label: "Series A" },
    { data: [28, 48, 40, 19, 86, 27, 90], label: "Series B" },
    {
      data: [180, 480, 770, 90, 1000, 270, 400],
      label: "Series C",
      yAxisID: "y-axis-1"
    }
  ];
  public lineChartLabels: Label[] = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July"
  ];
  public lineChartOptions: ChartOptions = {
    responsive: true,
    aspectRatio: 0.1,
    scales: {
      // We use this empty structure as a placeholder for dynamic theming.
      xAxes: [{}],
      yAxes: [
        {
          id: "y-axis-0",
          position: "left"
        },
        {
          id: "y-axis-1",
          position: "right",
          gridLines: {
            color: "rgba(255,0,0,0.3)"
          },
          ticks: {
            fontColor: "red"
          }
        }
      ]
    }
  };
  public lineChartColors: Color[] = [
    {
      // grey
      backgroundColor: "rgba(148,159,177,0.2)",
      borderColor: "rgba(148,159,177,1)",
      pointBackgroundColor: "rgba(148,159,177,1)",
      pointBorderColor: "#fff",
      pointHoverBackgroundColor: "#fff",
      pointHoverBorderColor: "rgba(148,159,177,0.8)"
    },
    {
      // dark grey
      backgroundColor: "rgba(77,83,96,0.2)",
      borderColor: "rgba(77,83,96,1)",
      pointBackgroundColor: "rgba(77,83,96,1)",
      pointBorderColor: "#fff",
      pointHoverBackgroundColor: "#fff",
      pointHoverBorderColor: "rgba(77,83,96,1)"
    },
    {
      // red
      backgroundColor: "rgba(255,0,0,0.3)",
      borderColor: "red",
      pointBackgroundColor: "rgba(148,159,177,1)",
      pointBorderColor: "#fff",
      pointHoverBackgroundColor: "#fff",
      pointHoverBorderColor: "rgba(148,159,177,0.8)"
    }
  ];
  public lineChartLegend = true;
  public lineChartType = "bar";
  //Device Status Chart Variables

  constructor(
    private breakpointObserver: BreakpointObserver,
    private _authService: AuthService,
    private http: HttpClient,
    private accAnalysisService: AccountanalysisdataserviceService,
    private _bottomSheet: MatBottomSheet
  ) {}

  openBottomSheet(): void {
    this._bottomSheet.open(BottomSheetComponent);
  }

  updateSpAdminDetails(value) {

    this.selectedSpAdminId = value;

    //Clearing Existing Data
    
    //Clearing Existing Data

    //Updating Values//

    //Headers//
    let headers = new HttpHeaders({
      Authorization: "Bearer " + `${sessionStorage.getItem("token")}`
    });
    //Headers//


    this.http //Fetching ratePlan for Particular SP Admin with AdminID
      .get(
        environment.ENV.baseURL +
          "/api/v1/accounts/ratePlan/?adminId="+this.selectedSpAdminId,
        { headers: headers }
      )
      .subscribe(res => {
        this.ratePlanChartData = []; 
        this.ratePlanChartLabels = [];
        this.topThreeRatePlanValues = [];
        let fetchedRatePlanData: any = res;
        for(let i of fetchedRatePlanData){
          this.topThreeRatePlanValues.push(i);
        }
        this.topThreeRatePlanValues.sort((a,b) => {
          return b.total - a.total;
        })
        this.topThreeRatePlanValues.splice(3,)
        for(let ratePlanData of fetchedRatePlanData){
          this.ratePlanChartData.push(ratePlanData.total);
          this.ratePlanChartLabels.push(ratePlanData.ratePlan);
        }
      });

      this.http //Fetching commPlan for Particular SP Admin with AdminID
      .get(
        environment.ENV.baseURL +
          "/api/v1/accounts/commPlan/?adminId=" +
          this.selectedSpAdminId,
        {
          headers: headers
        }
      )
      .subscribe(res => {
        this.commPlanChartData = [] 
        this.commPlanChartLabels = []
        this.topThreeCommPlanValues = [];
        let fetchedCommPlanData: any = res;
        for(let i of fetchedCommPlanData){
          this.topThreeCommPlanValues.push(i);
        }
        this.topThreeCommPlanValues.sort((a,b) => {
          return b.total - a.total;
        })

        this.topThreeCommPlanValues.splice(3,)

        for(let commPlanData of fetchedCommPlanData){
          this.commPlanChartData.push(commPlanData.total);
          this.commPlanChartLabels.push(commPlanData.communicationPlan);
        }
        console.log(fetchedCommPlanData)
      });

      this.http //Fetching accounts based on admin ID
      .get(
        environment.ENV.baseURL +
          "/api/v1/accounts/name?adminId=" +
          this.selectedSpAdminId,
        {
          headers: headers
        }
      )
      .subscribe(res => {
        this.accounts = [];
        let fetchedAccountNames: any = res;
        for(let account of fetchedAccountNames){
          this.accounts.push(account.accountName);
        }
      });
    //Updating Values//
  }

  ngOnInit() {
    
    Observable;
    timer(1, 1000).subscribe(() => {
      this.today = Date.now();
    });

    this.lastUpdatedTime = Date.now();

    let headers = new HttpHeaders({
      Authorization: "Bearer " + `${sessionStorage.getItem("token")}`
    });

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
        for (let i of fetchedData) {
          this.spAdminList.push(i);
        }

      this.http //Fetching ratePlan for Particular SP Admin with AdminID
      .get(
        environment.ENV.baseURL +
          "/api/v1/accounts/ratePlan/?adminId="+this.selectedSpAdminId,
        { headers: headers }
      )
      .subscribe(res => {
        let fetchedRatePlanData: any = res;
        for(let i of fetchedRatePlanData){
          this.topThreeRatePlanValues.push(i);
        }
        this.topThreeRatePlanValues.sort((a,b) => {
          return b.total - a.total;
        })
        this.topThreeRatePlanValues.splice(3,)
        for(let ratePlanData of fetchedRatePlanData){
          this.ratePlanChartData.push(ratePlanData.total);
          this.ratePlanChartLabels.push(ratePlanData.ratePlan);
        }
      });

      this.http //Fetching commPlan for Particular SP Admin with AdminID
      .get(
        environment.ENV.baseURL +
          "/api/v1/accounts/commPlan/?adminId=" +
          this.selectedSpAdminId,
        {
          headers: headers
        }
      )
      .subscribe(res => {
        let fetchedCommPlanData: any = res;
        for(let i of fetchedCommPlanData){
          this.topThreeCommPlanValues.push(i);
        }
        this.topThreeCommPlanValues.sort((a,b) => {
          return b.total - a.total;
        })

        this.topThreeCommPlanValues.splice(3,)

        for(let commPlanData of fetchedCommPlanData){
          this.commPlanChartData.push(commPlanData.total);
          this.commPlanChartLabels.push(commPlanData.communicationPlan);
        }
        console.log(fetchedCommPlanData)
      });

      this.http //Fetching accounts based on admin ID
      .get(
        environment.ENV.baseURL +
          "/api/v1/accounts/name?adminId=" +
          this.selectedSpAdminId,
        {
          headers: headers
        }
      )
      .subscribe(res => {
        let fetchedAccountNames: any = res;
        for(let account of fetchedAccountNames){
          this.accounts.push(account.accountName);
        }
      });
      });

    this.http //Fetching System Admin Stats
      .get(environment.ENV.baseURL + "/api/v1/admins/stats", {
        headers: headers
      })
      .subscribe(res => {
        this.deviceStatus = res;
        this.dataLoading = false;
      });
  }

  ngOnDestroy(): void {
    this._authService.logout();
  }
}
