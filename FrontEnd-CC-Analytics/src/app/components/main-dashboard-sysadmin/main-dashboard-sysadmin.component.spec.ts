import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MainDashboardSysadminComponent } from './main-dashboard-sysadmin.component';

describe('MainDashboardSysadminComponent', () => {
  let component: MainDashboardSysadminComponent;
  let fixture: ComponentFixture<MainDashboardSysadminComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MainDashboardSysadminComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MainDashboardSysadminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
