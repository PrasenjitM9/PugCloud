import {Component, OnInit} from '@angular/core';
import {FileDroovy, RequestService} from "../request.service";
import {AuthService} from '../auth.service';
import {UtilitaireService} from "../utilitaire.service";
import {User} from "../User";

@Component({
  selector: 'app-file-manager',
  templateUrl: './file-manager.component.html',
  styleUrls: ['./file-manager.component.css']
})
export class FileManagerComponent implements OnInit {


  constructor(private request: RequestService, private authService: AuthService, private utilitaire: UtilitaireService) {

  }

  protected fileList : FileDroovy[];
  private currentPath : string;
  private currentFolderId : string;


  ngOnInit() {
     this.initRoot();
     console.log(this.authService.user);
  }


  initRoot(){

    this.request.getFiles("root","root",this.authService.user.id).subscribe(
      data => {
        console.log(data);
        this.fileList = data;
        this.currentFolderId="root";
        this.currentPath="";
      });
  }

  onSelect(f :FileDroovy){

  if(f.type == "FOLDER"){
    this.currentPath+="/"+f.name;
    this.currentFolderId=f.id;

    this.request.getFiles(this.currentPath,this.currentFolderId,this.authService.user.id).subscribe(
      data => {
        console.log(data);
        this.fileList = data;
      });
  }


  }

  onSignOut(){
    this.authService.signOut();
  }

}
