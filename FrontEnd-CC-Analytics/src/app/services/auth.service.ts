import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { map } from "rxjs/operators";
import { environment } from "../../environments/environment";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Router } from '@angular/router';

@Injectable()
export class AuthService {
  // private readonly JWT_TOKEN = 'JWT_TOKEN';
  // private readonly REFREH_TOKEN = 'REFREH_TOKEN';
  // private loggedUser: string;

  redirectToUrl: string = '/dashboard.html';
  private loginUrl = environment.ENV.baseURL + "/login";
  private tokenUrl = environment.ENV.baseURL + "/oauth/token";

  constructor(private http: HttpClient, private router: Router) {}

  getToken(username, password): Observable<any> {
    const body = new HttpParams()
      .set("username", username)
      .set("password", password)
      .set("grant_type", "password");

    let headers = new HttpHeaders({
      "Content-type": "application/x-www-form-urlencoded; charset=utf-8",
      Authorization: "Basic " + btoa("ashish:secret")
    });
    //let options = new HttpRequest({ headers: headers });
    console.log(body.toString());
    console.log(headers);

    return this.http
      .post(this.tokenUrl, body.toString(), { headers: headers })
      .pipe(
        map(res => {
          return res;
        })
      );
  }

  loggedIn(){
      return !!sessionStorage.getItem('token');
  }

  logout(){
    sessionStorage.removeItem('token');
    this.router.navigate(['login.html']);
  }

  getTokenFromLocalStorage(){
      return sessionStorage.getItem('token');
  }
}
