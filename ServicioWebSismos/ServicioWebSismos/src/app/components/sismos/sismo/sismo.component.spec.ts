import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SismoComponent } from './sismo.component';

describe('SismoComponent', () => {
  let component: SismoComponent;
  let fixture: ComponentFixture<SismoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SismoComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SismoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
