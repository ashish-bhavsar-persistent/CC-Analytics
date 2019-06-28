import { Component, OnInit } from "@angular/core";
import { map } from "rxjs/operators";
import { Breakpoints, BreakpointObserver } from "@angular/cdk/layout";
import { NavBarComponent } from "../components/nav-bar/nav-bar.component";
import { AuthService } from "../services/auth.service";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { environment } from "../../environments/environment.prod";
import { Observable } from 'rxjs';
import { timer } from 'rxjs';
import { AccountanalysisdataserviceService } from '../services/accountanalysisdataservice.service';

@Component({
  selector: "app-main-dashboard",
  templateUrl: "./main-dashboard.component.html",
  styleUrls: ["./main-dashboard.component.scss"]
})
export class MainDashboardComponent implements OnInit {
  /** Based on the screen size, switch from standard to one column per row */
  cards = this.breakpointObserver.observe(Breakpoints.Handset).pipe(
    map(({ matches }) => {
      if (matches) {
        return [
          { title: "Card 1", cols: 1, rows: 1 },
          { title: "Card 2", cols: 1, rows: 1 },
          { title: "Card 3", cols: 1, rows: 1 },
          { title: "Card 4", cols: 1, rows: 1 }
        ];
      }

      return [
        { title: "Card 1", cols: 2, rows: 1 },
        { title: "Card 2", cols: 1, rows: 1 },
        { title: "Card 3", cols: 1, rows: 2 },
        { title: "Card 4", cols: 1, rows: 1 }
      ];
    })
  );

  username: string = "";
  roles: any;
  name: string;
  today = Date.now();
  placeholderData = "Accounts";
  firstDeviceStatus: any;
  secondDeviceStatus: any;
  thirdDeviceStatus: any;
  fourthDeviceStatus: any;
  device0 : any;
  device1 : any;
  device2 : any;
  device3 : any;

  planName: string = "Rate Plan"
  ratePlanDonutDisplay: boolean = true;
  commPlanDonutDisplay: boolean = false;

  constructor(
    private breakpointObserver: BreakpointObserver,
    private _authService: AuthService,
    private http: HttpClient,
    private accAnalysisService: AccountanalysisdataserviceService
  ) { }

  donutChartToggle(event) {
    if (event.checked) { this.commPlanDonutDisplay = true; this.ratePlanDonutDisplay = false; this.planName = "Comm Plan" }
    else { this.commPlanDonutDisplay = false; this.ratePlanDonutDisplay = true; this.planName = "Rate Plan" }
  }

  ngOnInit() {

    Observable
    timer(1, 1000).subscribe(() => {
      this.today = Date.now();
    })

    let headers = new HttpHeaders({
      Authorization: 'Bearer ' + `${sessionStorage.getItem("token")}`
    });

    this.http.get(environment.ENV.baseURL + '/api/v1/users/me', { headers: headers })
      .subscribe(res => {
        let userInfo: any = res;
        this.username = userInfo.username;
        this.roles = userInfo.roles;
        this.name = userInfo.name;
      })

    this.http.get(environment.ENV.baseURL + '/api/v1/accounts/deviceStatus', { headers: headers })
      .subscribe(res => {
        let deviceStatuses: any = res;
        if (deviceStatuses[0] !== undefined) {
          this.firstDeviceStatus = deviceStatuses[0];
          this.device0 = true;
        } if (deviceStatuses[1] !== undefined) {
          this.secondDeviceStatus = deviceStatuses[1];
          this.device1 = true;
        }
        if (deviceStatuses[2] !== undefined) {
          this.thirdDeviceStatus = deviceStatuses[2];
          this.device2 = true;
        }
        if (deviceStatuses[3] !== undefined) {
          this.fourthDeviceStatus = deviceStatuses[3];
          this.device3 = true;
        }
      })
  }

  ngOnDestroy(): void {
    this._authService.logout();
  }
}
