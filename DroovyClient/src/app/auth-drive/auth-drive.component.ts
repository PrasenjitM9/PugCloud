import {Component, OnInit} from '@angular/core';
import {UtilitaireService} from "../utilitaire.service";

@Component({
  selector: 'app-auth-drive',
  templateUrl: './auth-drive.component.html',
  styleUrls: ['./auth-drive.component.css']
})
export class AuthDriveComponent implements OnInit {

  private userID : string;

  constructor(private utilitaire: UtilitaireService) {
  }

  ngOnInit() {
    this.userID = this.utilitaire.readCookie("id");
  }
  authGoogleDrive() {

    var scope1 = "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive";
    var scope2 = " https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive.appdata";
    var scope3 = " https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive.apps.readonly";
    var scope4 = " https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive.file";
    var scope5 = " https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive.metadata";
    var scope6 = " https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive.metadata.readonly";
    var scope7 = " https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive.photos.readonly";
    var scope8 = " https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive.readonly";

    window.location.href = "https://accounts.google.com/o/oauth2/v2/auth?response_type=code&scope="
      +scope1
      +scope2
      +scope3
      +scope4
      +scope5
      +scope6
      +scope7
      +scope8
      +"&redirect_uri=http://localhost:8080/droovy/googledriveauth/callback&client_id=783584831345-rpngg6uic1i0iorvp2l5agc9ajmdm64v.apps.googleusercontent.com&prompt=consent&state="+this.userID;

  }

  authOneDrive(){
    window.location.href = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize?client_id=7da78622-f4f8-47d0-bbb0-9b777af993a7&response_type=code&redirect_uri=http://localhost:8080/droovy/onedriveauth/callback/&response_mode=query&scope=offline_access Files.Read Files.ReadWrite Files.Read.All Files.ReadWrite.All Sites.Read.All Sites.ReadWrite.All&state="+this.userID;
  }

  authDropBox() {
    window.location.href = "https://www.dropbox.com/oauth2/authorize?response_type=code&client_id=i90y72ofs47u9b8&redirect_uri=http://localhost:8080/droovy/dropboxauth/callback&state="+this.userID;
  }

}
