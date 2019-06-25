import { Injectable, Injector } from "@angular/core";
import { HttpInterceptor, HttpEvent, HttpErrorResponse } from "@angular/common/http";
import { AuthService } from "../services/auth.service";
import { Observable, of } from "rxjs";
import { catchError } from '../../../node_modules/rxjs/operators';
import { Router } from '../../../node_modules/@angular/router';

@Injectable()
export class TokenInterceptorService implements HttpInterceptor {
  constructor(private _injector: Injector, private auth: AuthService, private router: Router) { }

  intercept(req, next): Observable<HttpEvent<any>> {
    let _authService = this._injector.get(AuthService);
    const idToken = sessionStorage.getItem("token");
    let url: string = req.url
    if (idToken) {
      const cloned = req.clone({
        headers: req.headers.set("Authorization", "Bearer " + idToken)
      });
      return next.handle(cloned).pipe(catchError(x => this.handleErrors(x)));;
    }
    else if (!url.includes("api")) {
      console.log(url)
      return next.handle(req);
    }
    else return null;
  }

  private handleErrors(err: HttpErrorResponse): Observable<any> {
    if (err.status === 401) {
      this.auth.redirectToUrl = this.router.url;
      this.router.navigate(['/login.html']);
      return of("Not Authenticated");
    }
  }
}
