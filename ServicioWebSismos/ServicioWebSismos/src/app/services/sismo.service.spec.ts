import { TestBed } from '@angular/core/testing';

import { SismoService } from './sismo.service';

describe('SismoService', () => {
  let service: SismoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SismoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
