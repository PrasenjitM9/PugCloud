import { Component, OnInit } from '@angular/core';
import {AuthService} from "../auth.service";
import {User} from "../User";
import {ActivatedRoute, Router} from "@angular/router";
import {UtilitaireService} from "../utilitaire.service";

@Component({
  selector: 'app-callbackdriveconnection',
  templateUrl: './callbackdriveconnection.component.html',
  styleUrls: ['./callbackdriveconnection.component.css']
})
export class CallbackdriveconnectionComponent implements OnInit {

  private drive :string;
  private success :string;

  constructor(auth : AuthService,private router: Router,private route: ActivatedRoute,private util :UtilitaireService) {


    this.route.params.subscribe( params => this.drive = params.drive );
    this.route.params.subscribe( params => this.success=params.success );

    if(this.drive == "dropbox"){

      if(this.success=="false"){
        this.util.eraseCookie("connectedToDropbox");
        auth.user.connectedToDropbox = false;
      }
      else{
        this.util.createCookie("connectedToDropbox",true,1);
        auth.user.connectedToDropbox = true;
      }

    }
    else if(this.drive == "googledrive"){

      if(this.success=="false"){
        this.util.eraseCookie("connectedToGoogleDrive");
        auth.user.connectedToGoogleDrive = false;
      }
      else{
        this.util.createCookie("connectedToGoogleDrive",true,1);
        auth.user.connectedToGoogleDrive = true;
      }

    }
    else if(this.drive == "onedrive"){


      if(this.success=="false"){
        this.util.eraseCookie("connectedToOneDrive");
        auth.user.connectedToOneDrive = false;
      }
      else{
        this.util.createCookie("connectedToOneDrive",true,1);
        auth.user.connectedToOneDrive = true;
      }

    }

    this.router.navigateByUrl("/manager");

  }

  ngOnInit() {
  }

}
