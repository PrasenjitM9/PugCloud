import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FileDroovy, RequestService} from "../request.service";
import {AuthService} from "../auth.service";

@Component({
  selector: 'app-search-folder',
  templateUrl: './search-folder.component.html',
  styleUrls: ['./search-folder.component.css']
})
export class SearchFolderComponent implements OnInit {

  @Input() drive: string;
  @Output() choice = new EventEmitter<destinationMove>();


  private currentId = "root";
  private currentPath = "";
  private upId : string[] = [];
  private upPath : string[] = [];

  public currentFolderName : string;


  public listFolder : FileDroovy[];

  public google = 0;
  public one = 0 ;
  public dropbox = 0;

  public index = 0;


  constructor(private request : RequestService, private auth : AuthService) { }

  ngOnInit() {

    this.upId[this.index]="root";
    this.upPath[this.index]="root";

    this.updateDrive();


      this.request.getFiles("root","root", this.auth.user.id,this.google,this.one,this.dropbox,true).subscribe(
      data => {
        this.listFolder = data;
        console.log(data);

      });
  }

  goInFolder(folder : FileDroovy){
    this.updateDrive();

    this.index++;

    this.upId[this.index]=this.currentId;
    this.upPath[this.index]=this.currentPath;

    this.currentFolderName = folder.name;
    this.currentPath += "/"+folder.name;

    if(this.google){
      this.currentId = folder.sourceProperties["GoogleDrive"].id;
    }
    else if(this.one){
      this.currentId = folder.sourceProperties["Onedrive"].id;
    }
    else if(this.dropbox){
      this.currentId = folder.sourceProperties["Dropbox"].id;
    }

    console.log(this.currentId);
    console.log(this.currentPath);

    this.request.getFiles(this.currentPath,this.currentId, this.auth.user.id,this.google,this.one,this.dropbox,true).subscribe(
      data => {
        this.listFolder = data;

        console.log(data);

      });

  }


  chooseFolder(){
    this.choice.next({
      idParent : this.currentId,
      pathParent : this.currentPath
    });


  }

  back(){
    this.updateDrive();

    if(this.index > 0){

      this.currentPath = this.upPath[this.index];
      this.currentId = this.upId[this.index];
      this.index--;
    }

    this.request.getFiles(this.currentPath,this.currentId, this.auth.user.id,this.google,this.one,this.dropbox,true).subscribe(
      data => {
        this.listFolder = data;

        console.log(data);

      });
  }

  updateDrive(){



    if(this.drive=="googledrive"){
      this.google=1;
      this.dropbox=0;
      this.one=0;
    }
    else if(this.drive=="dropbox") {
      this.google=0;
      this.dropbox=1;
      this.one=0;
    }
    else{
      this.google=0;
      this.dropbox=0;
      this.one=1;
    }
  }



}
export interface destinationMove{
  idParent : string,
  pathParent : string

}
