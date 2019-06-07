import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) { }
  baseURL : string = environment.ENV.baseURL;

  login(loginPayload) {
    const headers = {
      'Authorization': 'Basic ' + btoa('ashish:secret'),
      'Content-type': 'application/x-www-form-urlencoded'
    }
    return this.http.post(this.baseURL + 'oauth/token', loginPayload, {headers});
  }

}
