import {Component, OnInit} from '@angular/core';
import {FileDroovy, RequestService} from "../request.service";
import {AuthService} from '../auth.service';
import {UtilitaireService} from "../utilitaire.service";
import {UploadDialog} from "../upload/upload.component";
import {MatDialog} from "@angular/material";
import {LoadingComponentComponent} from "../loading-component/loading-component.component";

@Component({
  selector: 'app-file-manager',
  templateUrl: './file-manager.component.html',
  styleUrls: ['./file-manager.component.css']
})
export class FileManagerComponent implements OnInit {

  constructor(public dialog: MatDialog,private request: RequestService, private authService: AuthService, private utilitaire: UtilitaireService) {
  }

  protected fileList : FileDroovy[];
  private userID : string;

  private space_info: SpaceInfo[] = [];

  tab_previous_folder: PreviousInfo[] = [];


  ngOnInit() {
    this.initUserId();

    //A MODIFIER
    //TO DO
    this.navigate("root", "root", this.authService.user.connectedToGoogleDrive ? 1 : 0,
      this.authService.user.connectedToOneDrive ? 1 : 0, this.authService.user.connectedToDropbox ? 1 : 0);

    this.updateFreespace()
  }

  initUserId(){
    this.userID = this.utilitaire.readCookie("id");
  }

  public navigate(path: string, idFolder: string, getGoogleDrive: number, getOneDrive: number, getDropbox: number) {
    this.tab_previous_folder.push(new PreviousInfo(path, idFolder, getGoogleDrive, getOneDrive, getDropbox));
    this.updateFileList(path, idFolder, getGoogleDrive, getOneDrive, getDropbox)
  }

  public navigatePrevious() {
    if (this.tab_previous_folder.length == 1) {
      return
    }
    this.tab_previous_folder.pop();
    var previous_info = this.tab_previous_folder[this.tab_previous_folder.length - 1];

    if (previous_info) {
      this.updateFileList(previous_info.path, previous_info.folder_id, previous_info.getGoogledrive, previous_info.getOnedrive, previous_info.getDropbox)
    }
  }


  public refreshList() {

    var lastindex = this.tab_previous_folder.length - 1;

    this.updateFileList(this.tab_previous_folder[lastindex].path, this.tab_previous_folder[lastindex].folder_id,
      this.tab_previous_folder[lastindex].getGoogledrive, this.tab_previous_folder[lastindex].getOnedrive,
      this.tab_previous_folder[lastindex].getDropbox)
  }


  private updateFileList(path: string, idFolder: string, getGoogleDrive: number, getOneDrive: number, getDropbox: number) {


    let dialogRef = this.dialog.open(LoadingComponentComponent, {
      data: {
        msg :  "Chargement ..."
      }
    });

    this.request.getFiles(path, idFolder, this.userID, getGoogleDrive, getOneDrive, getDropbox,false).subscribe(
      data => {
        this.fileList = data;
        dialogRef.close();

      });

  }

  public updateFreespace() {

    if (this.authService.user.connectedToDropbox) {
      this.space_info["dropbox"] = this.freespace("dropbox")
    }
    if (this.authService.user.connectedToDropbox) {
      this.space_info["googledrive"] = this.freespace("googledrive")
    }
    if (this.authService.user.connectedToDropbox) {
      this.space_info["onedrive"] = this.freespace("onedrive")
    }
  }

  private freespace(drive: string) {

    this.request.freespace(this.userID, drive).subscribe(
      data => {
        this.space_info[drive] = data
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
  }

}

class PreviousInfo {

  constructor(public path: string, public folder_id: string, public getGoogledrive: number, public getOnedrive: number,
              public getDropbox: number) {
  }

}

class SpaceInfo {
  quota: string;
  used: string;
  freeSpace: string
}
