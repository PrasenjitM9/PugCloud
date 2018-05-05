import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {Observable} from 'rxjs/Rx';

@Injectable()
export class AuthService {

  private apiUrl = "http://localhost:8080/droovy/";


  constructor(private http: HttpClient) { }

  createAccount(password :string,name : string) :Observable<AuthResult> {
    var url = this.apiUrl+"user/create?password="+password+"&name="+name;
    return this.http.get<AuthResult>(url, {responseType: 'json'});
  }

  connect(password :string,name : string) :Observable<AuthResult> {
    var url = this.apiUrl+"user/auth?password="+password+"&name="+name;
    return this.http.get<AuthResult>(url, {responseType: 'json'});
  }




}
export interface AuthResult{
  id : string;
  success : string;
  reason : string;
}
