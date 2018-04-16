import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';


@Injectable()
export class AuthService {

  private authUrlGoogleDrive = "https://accounts.google.com/o/oauth2/v2/auth?response_type=code&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive.metadata.readonly&redirect_uri=https://localhost&client_id=783584831345-rpngg6uic1i0iorvp2l5agc9ajmdm64v.apps.googleusercontent.com";


  constructor(private http: HttpClient) { }

  getToken() : string{
     this.http.get(this.authUrlGoogleDrive).subscribe(data => {
      return data;
    });
    return "";
  }


}
