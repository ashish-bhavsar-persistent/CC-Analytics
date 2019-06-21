import { TestBed } from '@angular/core/testing';

import { AccountanalysisdataserviceService } from './accountanalysisdataservice.service';

describe('AccountanalysisdataserviceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: AccountanalysisdataserviceService = TestBed.get(AccountanalysisdataserviceService);
    expect(service).toBeTruthy();
  });
});
