import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthDriveComponent } from './auth-drive.component';

describe('AuthDriveComponent', () => {
  let component: AuthDriveComponent;
  let fixture: ComponentFixture<AuthDriveComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AuthDriveComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AuthDriveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
