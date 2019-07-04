import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountBarChartComponent } from './account-bar-chart.component';

describe('AccountBarChartComponent', () => {
  let component: AccountBarChartComponent;
  let fixture: ComponentFixture<AccountBarChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccountBarChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountBarChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
