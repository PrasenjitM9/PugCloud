import {Component, EventEmitter, Input, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';
import {Router} from "@angular/router";
import {UtilitaireService} from "../utilitaire.service";
import {User} from "../User";

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css'],
})
export class AuthComponent implements OnInit {

  private user : User;

  constructor(private auth: AuthService, private router: Router, private utilitaire: UtilitaireService) {

  }

  ngOnInit() {
    this.user = this.auth.user;
  }

  connect(){

    this.auth.connect(this.password,this.name).subscribe(
      data => {

        if(data.success == "true") {

          this.utilitaire.createCookie("id", data['id'], 1);
          this.utilitaire.createCookie("isAuth", true, 1);
          this.router.navigateByUrl("/manager");

          this.user.isAuth = true;
          this.user.id = data.id;



        }
        else{
          this.user.isAuth = false;
        }
      }
    );

  }

  createAccount(){

     this.auth.createAccount(this.password,this.name).subscribe(
      data => {

        if(data.success == "true") {
          this.utilitaire.createCookie("id", data['id'], 1);
          this.utilitaire.createCookie("isAuth", true, 1);
          this.router.navigate(['/manager']);
          this.user.isAuth = true;
          this.user.id = data.id;

        }
        else{
          this.user.isAuth = false;
        }
      }
    );
  }



  @Input() name: string;
  @Input() password: string;
}
