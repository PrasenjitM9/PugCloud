import {inject, TestBed} from '@angular/core/testing';

import {UtilitaireService} from './utilitaire.service';

describe('UtilitaireService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UtilitaireService]
    });
  });

  it('should be created', inject([UtilitaireService], (service: UtilitaireService) => {
    expect(service).toBeTruthy();
  }));
});
