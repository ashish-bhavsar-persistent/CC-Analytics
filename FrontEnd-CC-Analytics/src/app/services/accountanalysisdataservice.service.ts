import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AccountanalysisdataserviceService {

  accountId: string = "";

  constructor(private http: HttpClient) { }

  setAccountId(accountId){
    this.accountId = accountId;
    console.log('acc service',this.accountId);
  }
  
  getAccountId(){
    return this.accountId;
  }
}
