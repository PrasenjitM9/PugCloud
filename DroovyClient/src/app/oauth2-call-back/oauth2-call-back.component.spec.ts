import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { Oauth2CallBackComponent } from './oauth2-call-back.component';

describe('Oauth2CallBackComponent', () => {
  let component: Oauth2CallBackComponent;
  let fixture: ComponentFixture<Oauth2CallBackComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ Oauth2CallBackComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(Oauth2CallBackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
