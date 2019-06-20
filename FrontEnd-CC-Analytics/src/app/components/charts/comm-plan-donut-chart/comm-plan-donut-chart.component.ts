import { Component, OnInit } from "@angular/core";
import { ChartType, ChartOptions } from "chart.js";
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Component({
  selector: "comm-plan-donut-chart",
  templateUrl: "./comm-plan-donut-chart.component.html",
  styleUrls: ["./comm-plan-donut-chart.component.scss"]
})
export class CommPlanDonutChartComponent implements OnInit {
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
  //public pieChartPlugins = [pluginDataLabels];
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

  constructor(private http: HttpClient) {}

  ngOnInit() {
    let headers = new HttpHeaders({
      Authorization: 'Bearer ' + `${sessionStorage.getItem("token")}`
    });

    this.http.get(environment.ENV.baseURL+'/api/v1/accounts/commPlan', {headers:headers})
    .subscribe(res => {
      let temp:any = res;
      for(let i of temp){
        this.pieChartData.push(i.total);
        this.pieChartLabels.push(i.communicationPlan);
      }
    })
  }
}
