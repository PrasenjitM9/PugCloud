import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Rx';
import {UtilitaireService} from "./utilitaire.service";
import {User} from "./User";

@Injectable()
export class AuthService {

  private apiUrl = "http://localhost:8080/droovy/";
  public user: User;

  constructor(private http: HttpClient, private utilitaire: UtilitaireService) {
    this.user = new User();
    this.setUser();
  }

  setUser() {

    if (this.utilitaire.readCookie("isAuth") == "true") {
      this.user.isAuth = true;
      this.user.id = this.utilitaire.readCookie("id");
      this.user.name = (this.utilitaire.readCookie("name"));
      this.user.connectedToDropbox = (this.utilitaire.readCookie("connectedToDropbox") == 'true');
      this.user.connectedToGoogleDrive = (this.utilitaire.readCookie("connectedToGoogleDrive") == 'true');
      this.user.connectedToOneDrive = (this.utilitaire.readCookie("connectedToOneDrive") == 'true');
    }

  }

  createAccount(password :string,name : string) :Observable<AuthResult> {

    if (name != this.utilitaire.readCookie("name")) {
      this.eraseUser();
    }

    this.utilitaire.createCookie("name", name, 1);
    var url = this.apiUrl+"user/create?password="+password+"&name="+name;
    return this.http.get<AuthResult>(url, {responseType: 'json'});
  }

  connect(password :string,name : string) :Observable<AuthResult> {

    if (name != this.utilitaire.readCookie("name")) {
      this.eraseUser();
    }


    this.utilitaire.createCookie("name", name, 1);
    var url = this.apiUrl+"user/auth?password="+password+"&name="+name;
    return this.http.get<AuthResult>(url, {responseType: 'json'});
  }

  signOut(){
    this.signOutUser();

  }

  eraseUser() {
    this.utilitaire.createCookie("isAuth", false, 1);
    this.utilitaire.eraseCookie("connectedToDropbox");
    this.utilitaire.eraseCookie("connectedToOneDrive");
    this.utilitaire.eraseCookie("connectedToGoogleDrive");
    this.utilitaire.eraseCookie("isAuth");
    this.utilitaire.eraseCookie("id");
    this.utilitaire.eraseCookie("name");
    this.user = new User();
  }

  signOutUser() {
    this.utilitaire.eraseCookie("isAuth");
    this.utilitaire.createCookie("isAuth", false, 1);
  }

}
export interface AuthResult{
  id : string;
  success : string;
  reason : string;
}

