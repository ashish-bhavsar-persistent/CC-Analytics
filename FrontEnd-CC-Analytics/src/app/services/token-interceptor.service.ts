import { Injectable, Injector } from "@angular/core";
import { HttpInterceptor, HttpEvent } from "@angular/common/http";
import { AuthService } from "../services/auth.service";
import { Observable } from "rxjs";

@Injectable()
export class TokenInterceptorService implements HttpInterceptor {
  constructor(private _injector: Injector) {}

  intercept(req, next): Observable<HttpEvent<any>> {
    let _authService = this._injector.get(AuthService);
    const idToken = sessionStorage.getItem("token");

    if (idToken) {
      const cloned = req.clone({
        headers: req.headers.set("Authorization", "Bearer " + idToken)
      });
      return next.handle(cloned);
    }
    else return next.handle(req);
  }
}
