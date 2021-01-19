import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SismosComponent } from './sismos.component';

describe('SismosComponent', () => {
  let component: SismosComponent;
  let fixture: ComponentFixture<SismosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SismosComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SismosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
