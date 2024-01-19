import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SensorDetailComponent } from './sensor-detail.component';

describe('SensorDetailComponent', () => {
  let component: SensorDetailComponent;
  let fixture: ComponentFixture<SensorDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SensorDetailComponent]
    });
    fixture = TestBed.createComponent(SensorDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
