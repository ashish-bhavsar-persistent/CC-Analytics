import { Component, OnInit, ViewChild, Inject } from "@angular/core";
import {
  map,
  distinctUntilChanged,
  debounceTime,
  merge,
  filter
} from "rxjs/operators";
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
import { ModalComponent } from "../components/modal/modal.component";
import { FormControl, FormGroup } from "@angular/forms";
import * as jwt_decode from "jwt-decode";
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from "@angular/material";

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
  private deviceDataUnavailable: boolean = true;
  private deviceDataAvailable: boolean = false;
  private topThreeRatePlanValues: any = [];
  private topThreeCommPlanValues: any = [];

  private deviceRatePlan: any = [];
  private deviceCommPlan: any = [];

  private spAdminList: any = [];

  private selectedSpAdminId: string = "";
  private deviceCount: number;

  private spAdminDataLoading: boolean = true;
  private dataLoaded: boolean = false;
  private loadSysadmin: boolean = false;
  private loadSpadmin: boolean = false;
  private loadUser: boolean = false;

  private accountParameter = {
    adminId: "",
    accountId: "",
    granularity: ""
  };

  stateCtrl = new FormControl();

  myForm = new FormGroup({
    state: this.stateCtrl
  });

  private accounts: string[] = [];

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
      backgroundColor: [
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
        "#de0000"
      ]
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

  public chartData = [
    { data: [], label: "monthly" },
    { data: [], label: "yearly" }
  ];
  public chartLabels = [];
  public chartOptions: any = {
    responsive: true,
    aspectRatio: 0.1,
    legend: {
      display: true
    },
    scales: {
      yAxes: [
        {
          display: true,
          stepSize: 1,
          gridLines: {
            drawOnChartArea: true
          },
          ticks: {
            maxTicksLimit: 8,
            beginAtZero: true
          }
        }
      ]
    },
    tooltips: {
      callbacks: {
        label: function(tooltipItem) {
          return tooltipItem.yLabel;
        }
      }
    }
  };
  public colorOptions: Color[] = [
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
  public chartLegend = true;
  public chartType = "bar";
  //Device Status Chart Variables

  constructor(
    private breakpointObserver: BreakpointObserver,
    private _authService: AuthService,
    private http: HttpClient,
    private accAnalysisService: AccountanalysisdataserviceService,
    private _bottomSheet: MatBottomSheet,
    private dialog: MatDialog
  ) {}

  openBottomSheet(): void {
    this._bottomSheet.open(BottomSheetComponent);
  }

  updateSpAdminDetails(value) {
    this.selectedSpAdminId = value;
    this.accountParameter.adminId = this.selectedSpAdminId;
    //Updating Values//

    //Headers//
    let headers = new HttpHeaders({
      Authorization: "Bearer " + `${sessionStorage.getItem("token")}`
    });
    //Headers//

    this.http //Fetching ratePlan for Particular SP Admin with AdminID
      .get(
        environment.ENV.baseURL +
          "/api/v1/accounts/ratePlan/?adminId=" +
          this.selectedSpAdminId,
        { headers: headers }
      )
      .subscribe(res => {
        this.ratePlanChartData = [];
        this.ratePlanChartLabels = [];
        this.topThreeRatePlanValues = [];
        let fetchedRatePlanData: any = res;
        for (let i of fetchedRatePlanData) {
          this.topThreeRatePlanValues.push(i);
        }
        this.topThreeRatePlanValues.sort((a, b) => {
          return b.total - a.total;
        });
        this.topThreeRatePlanValues.splice(3);
        for (let ratePlanData of fetchedRatePlanData) {
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
        this.commPlanChartData = [];
        this.commPlanChartLabels = [];
        this.topThreeCommPlanValues = [];
        let fetchedCommPlanData: any = res;
        for (let i of fetchedCommPlanData) {
          this.topThreeCommPlanValues.push(i);
        }
        this.topThreeCommPlanValues.sort((a, b) => {
          return b.total - a.total;
        });

        this.topThreeCommPlanValues.splice(3);

        for (let commPlanData of fetchedCommPlanData) {
          this.commPlanChartData.push(commPlanData.total);
          this.commPlanChartLabels.push(commPlanData.communicationPlan);
        }
        console.log(fetchedCommPlanData);
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
        for (let account of fetchedAccountNames) {
          this.accounts.push(account.accountName + " : " + account.accountId);
        }
      });

    this.http //Fetching accounts based on admin ID
      .get(
        environment.ENV.baseURL +
          "/api/v1/accounts/deviceCount?adminId=" +
          this.selectedSpAdminId,
        {
          headers: headers
        }
      )
      .subscribe(res => {
        this.deviceCount = res;
      });

    //Updating Values//
  }

  updateAccountDetails() {
    this.deviceDataUnavailable = false;
    this.deviceDataAvailable = true;

    let headers = new HttpHeaders({
      Authorization: "Bearer " + `${sessionStorage.getItem("token")}`
    });

    let splittedArray = this.accountParameter.accountId.split(":");
    let accountId = splittedArray[1].trim();

    this.http
      .get(
        environment.ENV.baseURL +
          "/api/v1/devices/status?adminId=" +
          this.accountParameter.adminId +
          "&accountId=" +
          accountId,
        { headers: headers }
      )
      .subscribe(res => {
        let accountChartData: any = res;
        this.chartData[0].data = [];
        this.chartData[1].data = [];
        this.chartLabels = [];
        for (let account of accountChartData) {
          this.chartData[0].data.push(account.monthlyCount);
          this.chartData[1].data.push(account.yearlyCount);
          this.chartLabels.push(account.status);
        }
      });

    this.http
      .get(
        environment.ENV.baseURL +
          "/api/v1/devices/ratePlan?adminId=" +
          this.accountParameter.adminId +
          "&accountId=" +
          accountId,
        { headers: headers }
      )
      .subscribe(res => {
        let deviceRateData: any = res;
        this.deviceRatePlan = deviceRateData;
      });

    this.http
      .get(
        environment.ENV.baseURL +
          "/api/v1/devices/commPlan?adminId=" +
          this.accountParameter.adminId +
          "&accountId=" +
          accountId,
        { headers: headers }
      )
      .subscribe(res => {
        let deviceCommData: any = res;
        this.deviceCommPlan = deviceCommData;
      });
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(ModalComponent, {
      width: "250px"
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log("The dialog was closed");
    });
  }

  ngOnInit() {
    let temp: any = jwt_decode(sessionStorage.getItem("token"));
    console.log(temp);

    if (temp["authorities"].includes("ROLE_SYSADMIN")) {
      this.loadSysadmin = true;
    } else if (temp["authorities"].includes("ROLE_ADMIN")) {
      this.loadSpadmin = true;
    } else {
      this.loadUser = true;
    }

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
        this.accountParameter.adminId = this.selectedSpAdminId;
        for (let i of fetchedData) {
          this.spAdminList.push(i);
        }

        this.http //Fetching accounts based on admin ID
          .get(
            environment.ENV.baseURL +
              "/api/v1/accounts/deviceCount?adminId=" +
              this.selectedSpAdminId,
            {
              headers: headers
            }
          )
          .subscribe(res => {
            this.deviceCount = res;
          });

        this.http //Fetching ratePlan for Particular SP Admin with AdminID
          .get(
            environment.ENV.baseURL +
              "/api/v1/accounts/ratePlan/?adminId=" +
              this.selectedSpAdminId,
            { headers: headers }
          )
          .subscribe(res => {
            let fetchedRatePlanData: any = res;
            for (let i of fetchedRatePlanData) {
              this.topThreeRatePlanValues.push(i);
            }
            this.topThreeRatePlanValues.sort((a, b) => {
              return b.total - a.total;
            });
            this.topThreeRatePlanValues.splice(3);
            for (let ratePlanData of fetchedRatePlanData) {
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
            for (let i of fetchedCommPlanData) {
              this.topThreeCommPlanValues.push(i);
            }
            this.topThreeCommPlanValues.sort((a, b) => {
              return b.total - a.total;
            });

            this.topThreeCommPlanValues.splice(3);

            for (let commPlanData of fetchedCommPlanData) {
              this.commPlanChartData.push(commPlanData.total);
              this.commPlanChartLabels.push(commPlanData.communicationPlan);
            }
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
            for (let account of fetchedAccountNames) {
              this.accounts.push(
                account.accountName + " : " + account.accountId
              );
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
