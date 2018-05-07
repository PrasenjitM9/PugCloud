import { Component, OnInit } from '@angular/core';
import {FileDroovy, RequestService} from "../request.service";
import { AuthService} from '../auth.service';

@Component({
  selector: 'app-file-manager',
  templateUrl: './file-manager.component.html',
  styleUrls: ['./file-manager.component.css']
})
export class FileManagerComponent implements OnInit {

  authStatus : boolean;

  constructor(private request: RequestService, private authService:AuthService) { }

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
    this.userID = this.readCookie("id");
  }

  initRoot(){

    this.request.getFiles("root","root",this.userID).subscribe(
      data => {
        console.log(data);
        this.fileList = data;
        this.currentFolderId="root";
        this.currentPath="";
      });
  }


  readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
      var c = ca[i];
      while (c.charAt(0)==' ') c = c.substring(1,c.length);
      if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
  }

  onSelect(f :FileDroovy){

  if(f.type == "FOLDER"){
    this.currentPath+="/"+f.name;
    this.currentFolderId=f.id;

    this.request.getFiles(this.currentPath,this.currentFolderId,this.userID).subscribe(
      data => {
        console.log(data);
        this.fileList = data;
      });
  }


  }

  onSignOut(){
    this.authService.signOut();
    this.authStatus = this.authService.isAuth;
  }

}
