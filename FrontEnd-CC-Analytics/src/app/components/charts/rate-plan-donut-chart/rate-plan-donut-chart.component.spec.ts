import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RatePlanDonutChartComponent } from './rate-plan-donut-chart.component';

describe('RatePlanDonutChartComponent', () => {
  let component: RatePlanDonutChartComponent;
  let fixture: ComponentFixture<RatePlanDonutChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RatePlanDonutChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RatePlanDonutChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
