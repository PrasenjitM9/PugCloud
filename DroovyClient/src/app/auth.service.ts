import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Rx';
import {UtilitaireService} from "./utilitaire.service";
import {User} from "./User";

@Injectable()
export class AuthService {

  private apiUrl = "http://localhost:8080/droovy/";
  public user : User;

  constructor(private http: HttpClient, private utilitaire: UtilitaireService) {
    this.user = new User();
    this.user.isAuth = false;
    this.setUser(this.user);
  }

  setUser(user : User){
    this.user = user;
    if (this.utilitaire.readCookie("isAuth") == "true") {
      this.user.isAuth = true;
      this.user.id = this.utilitaire.readCookie("id");
    }
  }

  createAccount(password :string,name : string) :Observable<AuthResult> {
    var url = this.apiUrl+"user/create?password="+password+"&name="+name;
    return this.http.get<AuthResult>(url, {responseType: 'json'});
  }

  connect(password :string,name : string) :Observable<AuthResult> {
    var url = this.apiUrl+"user/auth?password="+password+"&name="+name;
    return this.http.get<AuthResult>(url, {responseType: 'json'});
  }

  signOut(){
    this.utilitaire.createCookie("isAuth", false, 1)
    this.user.isAuth=false;
  }


}
export interface AuthResult{
  id : string;
  success : string;
  reason : string;
}

