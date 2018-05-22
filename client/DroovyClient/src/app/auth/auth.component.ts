import {Component, Input, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';
import {Router} from "@angular/router";
import {UtilitaireService} from "../utilitaire.service";
import {MatDialog, MatSnackBar} from "@angular/material";
import {ErrorDialogComponent} from "../error-dialog/error-dialog.component";

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css'],
})
export class AuthComponent implements OnInit {


  constructor(private snackBar: MatSnackBar,private auth: AuthService, private router: Router, private utilitaire: UtilitaireService) {

  }

  ngOnInit() {
  }

  connect(){

    this.auth.connect(this.password,this.name).subscribe(
      data => {

        if(data.success == "true") {

          this.utilitaire.createCookie("id", data['id'], 1);
          this.utilitaire.createCookie("isAuth", true, 1);
          this.auth.setUser();
          this.router.navigateByUrl("/manager");

        }
        else{
          this.auth.user.isAuth = false;
          this.snackBar.open('Mauvais identifiant ou mot de passe', 'Ok', {
            duration: 3000
          });
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
          this.auth.setUser();
          this.router.navigateByUrl("/manager");

        }
        else{
          this.auth.user.isAuth = false;

          if(data.reason!=undefined){
            if(data.reason=="too short"){
              this.snackBar.open('Trop court, 3 caractères minimum', 'Ok', {
                duration: 3000
              });
            }
            else if(data.reason=="alreadyExist"){
              this.snackBar.open('Nom déjà utilisé', 'Ok', {
                duration: 3000
              });
            }

          }
          else{
            this.snackBar.open('Impossible de créer le compte', 'Ok', {
              duration: 3000
            });
          }

        }
      }
    );
  }


  @Input() name: string;
  @Input() password: string;
}
