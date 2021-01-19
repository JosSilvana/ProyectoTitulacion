import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SismosListComponent } from './sismos-list.component';

describe('SismosListComponent', () => {
  let component: SismosListComponent;
  let fixture: ComponentFixture<SismosListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SismosListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SismosListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
