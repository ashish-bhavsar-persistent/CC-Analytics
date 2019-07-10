import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SpadminDashboardComponent } from './spadmin-dashboard.component';

describe('SpadminDashboardComponent', () => {
  let component: SpadminDashboardComponent;
  let fixture: ComponentFixture<SpadminDashboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SpadminDashboardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SpadminDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
