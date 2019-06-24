import { Component, OnInit, Input } from '@angular/core';
import { ChartType, ChartOptions } from "chart.js";
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { AccountanalysisdataserviceService } from '../../../services/accountanalysisdataservice.service';

export interface User {
  accountName: string;
  accountId: string;
}

@Component({
  selector: 'app-acct-analytics-piechart',
  templateUrl: './acct-analytics-piechart.component.html',
  styleUrls: ['./acct-analytics-piechart.component.scss']
})
export class AcctAnalyticsPiechartComponent implements OnInit {
  placeholderData: string;
  myControl = new FormControl();
  options: User[] = [];
  filteredOptions: Observable<User[]>;
  acctId : any;
  checked: false;
  rateOrCommPlanUrl : any;
  public pieChartData = [];
  public pieChartLabels = [];
  public pieChartOptions: ChartOptions = {
    responsive: true,
    aspectRatio: 1,
    legend: {
      position: "top"
    },
    elements: {
      arc: {
        borderWidth: 0
      }
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
  public pieChartType: ChartType = "pie";
  public pieChartLegend = false;
  public pieChartColors = [
    {
      backgroundColor: [
        "#9d70c2",
        "#c46aba",
        "#e564aa",
        "#ff6294",
        "#ff6978",
        "#ff7859",
        "#ff8e37",
        "#ffa600",
        "#ffa765"
      ]
    }
  ];

  constructor(private http: HttpClient, private accAnalysisService: AccountanalysisdataserviceService) {

  }

  ngOnInit() {
    this.rateOrCommPlanUrl=environment.ENV.baseURL + '/api/v1/devices/commPlan?accountId=';
    this.placeholderData = "Accounts";
    this.acctId = "";

    console.log("value of placeholder data ", this.placeholderData);
    let headers = new HttpHeaders({
      Authorization: 'Bearer ' + `${sessionStorage.getItem("token")}`
    });

    this.http.get(environment.ENV.baseURL + '/api/v1/accounts/name', { headers: headers })
      .subscribe(res => {
        let temp: any = res;
        for (let i of temp) {
          this.options.push(i);
        }
        this.filteredOptions = this.myControl.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filter(value))
          );



      })
  }
  toggleRateAndCommPlan(){
    console.log('toggleRateAndCommPlan ',this.checked);
    if(this.checked){
      this.rateOrCommPlanUrl = environment.ENV.baseURL + '/api/v1/devices/ratePlan?accountId=';
      this.fetchPlanDetails(this.acctId);
    }
    else {
      this.rateOrCommPlanUrl = environment.ENV.baseURL + '/api/v1/devices/commPlan?accountId='
      this.fetchPlanDetails(this.acctId);
    }
  }

  fetchPlanDetails(accountIdValue){
    console.log('in fetchPlanDetails ',accountIdValue);
    let headers = new HttpHeaders({
      Authorization: 'Bearer ' + `${sessionStorage.getItem("token")}`
    });
    this.http.get(this.rateOrCommPlanUrl+accountIdValue, { headers: headers })
    .subscribe(res => {
      this.pieChartData = [];
      let temp: any = res;
      for (let i of temp) {
        this.pieChartData.push(i.total);
        this.pieChartLabels.push(i.communicationPlan);
      }
    })
  }

  fetchAccountDetails(event) {
    let accountDetails: any = event.option.value;
    let accountId: string = accountDetails.accountId;
    this.accAnalysisService.setAccountId(accountId);
    this.acctId = accountDetails.accountId;
    console.log('in fetchAccountDetails event ',this.acctId);
    this.fetchPlanDetails(this.acctId);
  }

  displayFn(user?: User): string | undefined {
    return user ? user.accountName : undefined;
  }

  private _filter(name: string): User[] {
    if (name !== null && name !== '' && typeof name !== "string") {
      let userJSON: any = name;
      name = userJSON.accountName;
    }

    const filterValue = name.toLowerCase();
    return this.options.filter(option => option.accountName.toLowerCase().includes(filterValue));
  }

}
