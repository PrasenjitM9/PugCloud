import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchFolderComponent } from './search-folder.component';

describe('SearchFolderComponent', () => {
  let component: SearchFolderComponent;
  let fixture: ComponentFixture<SearchFolderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SearchFolderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchFolderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
