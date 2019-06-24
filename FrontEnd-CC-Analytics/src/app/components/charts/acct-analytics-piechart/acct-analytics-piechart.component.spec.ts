import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AcctAnalyticsPiechartComponent } from './acct-analytics-piechart.component';

describe('AcctAnalyticsPiechartComponent', () => {
  let component: AcctAnalyticsPiechartComponent;
  let fixture: ComponentFixture<AcctAnalyticsPiechartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AcctAnalyticsPiechartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AcctAnalyticsPiechartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
