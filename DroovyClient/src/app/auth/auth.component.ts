import {Component, Input, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';
import {Router} from "@angular/router";
import {UtilitaireService} from "../utilitaire.service";

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css'],
})
export class AuthComponent implements OnInit {

  constructor(private auth: AuthService, private router: Router, private utilitaire: UtilitaireService) {
  }

  ngOnInit() {

  }

  connect(){
    this.auth.connect(this.password,this.name).subscribe(
      data => {
        console.log(data);


        if(data.success == "true") {

          this.utilitaire.createCookie("id", data['id'], 1);
          this.utilitaire.createCookie("isAuth", true, 1);
         // this.router.navigate(['/manager']);
          this.router.navigateByUrl("/manager");
        }
        else{

        }
      }
    );

  }

  createAccount(){

    alert("create")

     this.auth.createAccount(this.password,this.name).subscribe(
      data => {
        console.log(data);

        if(data.success == "true") {
          this.utilitaire.createCookie("id", data['id'], 1);
          this.utilitaire.createCookie("isAuth", true, 1);
          this.router.navigate(['/manager']);

        }
        else{

        }
      }
    );
  }


  @Input() name: string;
  @Input() password: string;
}
