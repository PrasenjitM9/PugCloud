import { Component, OnInit } from '@angular/core';
import {FileDroovy, RequestService} from "../request.service";

@Component({
  selector: 'app-file-manager',
  templateUrl: './file-manager.component.html',
  styleUrls: ['./file-manager.component.css']
})
export class FileManagerComponent implements OnInit {

  constructor(private request: RequestService) { }

  protected fileList : FileDroovy[];
  private userID : string;

  ngOnInit() {
     this.initUserId();
     this.initRoot();
  }

  initUserId(){
    this.userID = this.readCookie("id");
  }

  initRoot(){

    this.request.getFiles("root","root",this.userID).subscribe(
      data => {
        console.log(data);
        this.fileList = data;
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

}
