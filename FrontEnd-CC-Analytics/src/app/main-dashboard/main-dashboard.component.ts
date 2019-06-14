import { Component, OnInit } from "@angular/core";
import { map } from "rxjs/operators";
import { Breakpoints, BreakpointObserver } from "@angular/cdk/layout";
import { NavBarComponent } from "../components/nav-bar/nav-bar.component";
import { AuthService } from "../services/auth.service";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { environment } from "../../environments/environment";
import { Observable } from 'rxjs';
import { timer } from 'rxjs';

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

  username : string = "";
  roles: any;
  name: string;
  today = Date.now();
  placeholderData = "hello";
  placeholderData2 = "hell222o";

  constructor(
    private breakpointObserver: BreakpointObserver,
    private _authService: AuthService,
    private http: HttpClient
  ) {}

  ngOnInit() {

    Observable
    timer(1,1000).subscribe(() => {
      this.today = Date.now();
    })

    let headers = new HttpHeaders({
      Authorization: 'Bearer ' + `${sessionStorage.getItem("token")}`
    });

    this.http.get(environment.ENV.baseURL+'/api/v1/users/me', { headers:headers })
    .subscribe(res => {
      let userInfo:any = res;
      this.username = userInfo.username;
      this.roles = userInfo.roles;
      this.name = userInfo.name;
    })
  }

  ngOnDestroy(): void {
    this._authService.logout();
  }
}
