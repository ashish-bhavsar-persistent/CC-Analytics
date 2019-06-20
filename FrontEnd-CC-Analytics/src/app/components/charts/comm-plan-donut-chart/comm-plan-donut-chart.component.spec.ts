import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CommPlanDonutChartComponent } from './comm-plan-donut-chart.component';

describe('CommPlanDonutChartComponent', () => {
  let component: CommPlanDonutChartComponent;
  let fixture: ComponentFixture<CommPlanDonutChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CommPlanDonutChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommPlanDonutChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
