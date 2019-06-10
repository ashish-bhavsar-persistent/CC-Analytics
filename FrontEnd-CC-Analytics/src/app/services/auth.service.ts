import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class AuthService {

    // private readonly JWT_TOKEN = 'JWT_TOKEN';
    // private readonly REFREH_TOKEN = 'REFREH_TOKEN';
    // private loggedUser: string;

    private loginUrl = environment.ENV.baseURL + "/login";
    private tokenUrl = environment.ENV.baseURL + "/oauth/token";

    constructor(private http: HttpClient){}

    

    getToken(username, password):Observable<any>{

        const body = new HttpParams()
        .set('username', username)
        .set('password', password)
        .set('grant_type','password');

        let headers = new HttpHeaders();
        headers.set('Content-Type', 'application/x-www-form-urlencoded');
        headers.set('Authorization', 'Basic YXNoaXNoOnNlY3JldA==');

        console.log(body.toString());
        console.log(headers)

        return this.http.post(this.tokenUrl, body.toString(), {
            withCredentials: true,
            headers:{ 'Authorization' : 'Basic YXNoaXNoOnNlY3JldA=='}
        } )
        .pipe(map (res => {
            return res;
        }))
    }

}