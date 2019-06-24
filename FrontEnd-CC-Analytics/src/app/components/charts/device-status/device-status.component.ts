import { Component, OnInit, Input } from '@angular/core';
import { Label } from 'ng2-charts';
import { ChartOptions, ChartType, ChartDataSets } from 'chart.js';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment.prod';
import { startWith, map } from 'rxjs/operators';

export interface Account {
  accountName: string;
  accountId: string;
}



@Component({
  selector: 'device-status',
  templateUrl: './device-status.component.html',
  styleUrls: ['./device-status.component.scss']
})
export class DeviceStatusComponent implements OnInit {

  myControl = new FormControl();
  granularityControl = new FormControl();
  options: Account[] = [];
  filteredOptions: Observable<Account[]>;
  headers = new HttpHeaders({
    Authorization: 'Bearer ' + `${sessionStorage.getItem("token")}`
  });
  accountId: string = '';
  granularity: string = '';

  public barChartOptions: ChartOptions = {
    responsive: true,
    aspectRatio: 1,
    // We use these empty structures as placeholders for dynamic theming.
    scales: {
      xAxes: [{}], yAxes: [{
        ticks: {
          beginAtZero: true
        }
      }]
    },
    plugins: {
      datalabels: {
        anchor: 'end',
        align: 'end',
      }
    }
  };
  public barChartLabels: Label[] = [];
  public barChartType: ChartType = 'bar';
  public barChartLegend = false;


  public barChartData: ChartDataSets[] = [
    { data: [] }
  ];


  constructor(private http: HttpClient) { }

  ngOnInit() {


    this.http.get(environment.ENV.baseURL + '/api/v1/accounts/name', { headers: this.headers })
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


  fetchAccountDetails(event) {
    let accountDetails: any = event.option.value;
    // let accountId: string = accountDetails.accountId;
    this.accountId = accountDetails.accountId;
    if (this.granularity !== '') {
      this.fetchDeviceStatus()
    }
  }

  onChange(granularity) {
    this.granularity = granularity;
    if (this.accountId !== '') {
      this.fetchDeviceStatus()
    }
  }

  displayFn(user?: Account): string | undefined {
    return user ? user.accountName : undefined;
  }

  fetchDeviceStatus() {
    this.barChartData[0].data = [];
    this.barChartLabels = [];
    this.http.get(environment.ENV.baseURL + '/api/v1/devices/status?accountId=' + this.accountId + '&granularity=' + this.granularity, { headers: this.headers })
      .subscribe(res => {
        let temp: any = res;
        for (let i of temp) {
          this.barChartData[0].data.push(i.total);
          this.barChartLabels.push(i.status);
        }
      })
  }

  private _filter(name: string): Account[] {
    if (name !== null && name !== '' && typeof name !== "string") {
      let userJSON: any = name;
      name = userJSON.accountName;
    }

    const filterValue = name.toLowerCase();
    return this.options.filter(option => option.accountName.toLowerCase().includes(filterValue));
  }
}
