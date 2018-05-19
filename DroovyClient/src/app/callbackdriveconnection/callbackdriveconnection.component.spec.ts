import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CallbackdriveconnectionComponent } from './callbackdriveconnection.component';

describe('CallbackdriveconnectionComponent', () => {
  let component: CallbackdriveconnectionComponent;
  let fixture: ComponentFixture<CallbackdriveconnectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CallbackdriveconnectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CallbackdriveconnectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
