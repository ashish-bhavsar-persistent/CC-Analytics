import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountAnalysisChartComponent } from './account-analysis-chart.component';

describe('AccountAnalysisChartComponent', () => {
  let component: AccountAnalysisChartComponent;
  let fixture: ComponentFixture<AccountAnalysisChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccountAnalysisChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountAnalysisChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
