import { TestBed } from '@angular/core/testing';

import { ApiconnectionService } from './apiconnection.service';

describe('ApiconnectionService', () => {
  let service: ApiconnectionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiconnectionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
