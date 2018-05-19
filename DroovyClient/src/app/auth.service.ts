import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Rx';
import {UtilitaireService} from "./utilitaire.service";

@Injectable()
export class AuthService {
  isAuth = false;

  private apiUrl = "http://localhost:8080/droovy/";


  constructor(private http: HttpClient, private utilitaire: UtilitaireService) {

    if (utilitaire.readCookie("isAuth") == "true") {
      this.isAuth = true;
    }
  }

  createAccount(password :string,name : string) :Observable<AuthResult> {
    var url = this.apiUrl+"user/create?password="+password+"&name="+name;
    this.isAuth=true;
    return this.http.get<AuthResult>(url, {responseType: 'json'});
  }

  connect(password :string,name : string) :Observable<AuthResult> {
    var url = this.apiUrl+"user/auth?password="+password+"&name="+name;
    this.isAuth=true;
    return this.http.get<AuthResult>(url, {responseType: 'json'});
  }

  signOut(){
    this.utilitaire.createCookie("isAuth", false, 1)
    this.isAuth=false;
  }


}
export interface AuthResult{
  id : string;
  success : string;
  reason : string;
}
