import {Component, OnInit} from '@angular/core';
import {FileDroovy, RequestService} from "../request.service";
import {AuthService} from '../auth.service';
import {UtilitaireService} from "../utilitaire.service";

@Component({
  selector: 'app-file-manager',
  templateUrl: './file-manager.component.html',
  styleUrls: ['./file-manager.component.css']
})
export class FileManagerComponent implements OnInit {

  authStatus : boolean;

  constructor(private request: RequestService, private authService: AuthService, private utilitaire: UtilitaireService) {
  }

  protected fileList : FileDroovy[];
  private userID : string;
  private currentPath : string;
  private currentFolderId : string;

  ngOnInit() {
     this.initUserId();
     this.initRoot();
     this.authStatus=this.authService.isAuth;
  }

  initUserId(){
    this.userID = this.utilitaire.readCookie("id");
  }

  initRoot(){

    this.request.getFiles("root", "root", this.userID, 1, 0, 1).subscribe(
      data => {

        this.fileList = data;

        this.currentFolderId="root";
        this.currentPath="";

      });

  }

  /*
    onSelect(f :FileDroovy){

    if(f.type == "FOLDER"){
      this.currentPath+="/"+f.name;
      this.currentFolderId=f.id;

      this.request.getFiles(this.currentPath,this.currentFolderId,this.userID,1, 0, 1).subscribe(
        data => {
          console.log(data);
          this.fileList = data;
        });
    }



    }
  */


  onSignOut(){
    this.authService.signOut();
    this.authStatus = this.authService.isAuth;
  }

}
