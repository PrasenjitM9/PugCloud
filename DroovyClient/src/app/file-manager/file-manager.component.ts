import {Component, Input, OnInit} from '@angular/core';
import {FileDroovy, RequestService, token} from "../request.service";
import {AuthService} from '../auth.service';
import {UtilitaireService} from "../utilitaire.service";
import {MatDialog} from "@angular/material";
import {LoadingComponentComponent} from "../loading-component/loading-component.component";
import {ErrorDialogComponent} from "../error-dialog/error-dialog.component";
import {CreateFolderComponent} from "../create-folder/create-folder.component";
import {Router} from '@angular/router';
import {Observable} from "../../../node_modules/rxjs";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-file-manager',
  templateUrl: './file-manager.component.html',
  styleUrls: ['./file-manager.component.css']
})
export class FileManagerComponent implements OnInit {

  constructor(public dialog: MatDialog,private request: RequestService, private authService: AuthService, private utilitaire: UtilitaireService, private router : Router) {
  }
  @Input() searchQuery: string;

  public fileList: FileDroovy[] = [];
  public tab_fileList: Array<FileDroovy>[] = [];
  private userID : string;

  public space_info: SpaceInfo[] = [];

  public tab_previous_folder: PreviousInfo[] = [];


  private pageTokenDropbox : token;
  private pageTokenOneDrive : token;
  private pageTokenGoogleDrive : token;


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


  public internSearch(){

  }

  public refreshList() {

    var lastindex = this.tab_previous_folder.length - 1;

    this.updateFileList(this.tab_previous_folder[lastindex].path, this.tab_previous_folder[lastindex].folder_id,
      this.tab_previous_folder[lastindex].getGoogledrive, this.tab_previous_folder[lastindex].getOnedrive,
      this.tab_previous_folder[lastindex].getDropbox)
  }

  divide_file_list(fileList: FileDroovy[]): Array<FileDroovy[]> {

    var tab_fileList = [];

    while (fileList.length != 0) {
      tab_fileList.push(fileList.slice(0, 5));
      fileList = fileList.slice(5)
    }
    return tab_fileList
  }

  public createFolder(){
    let dialogRef = this.dialog.open(CreateFolderComponent, {
      data: {
        folder :  this.tab_previous_folder[this.tab_previous_folder.length-1]
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      this.refreshList();
    });
  }

  handleError(error : any){
   console.log("Erreur : "+error.httpErrorCode);
    let dialogRef = this.dialog.open(ErrorDialogComponent, {
      data: {
        msg :  "Oups, ....\nUne erreur est survenue, l'action a échoué"
      }
    });
  }
  public updateFreespace() {

    if (this.authService.user.connectedToDropbox) {
      this.space_info["dropbox"] = this.freespace("dropbox")
    }
    if (this.authService.user.connectedToGoogleDrive) {
      this.space_info["googledrive"] = this.freespace("googledrive")
    }
    if (this.authService.user.connectedToOneDrive) {
      this.space_info["onedrive"] = this.freespace("onedrive")
    }
  }

  private freespace(drive: string) {

    this.request.freespace(this.userID, drive).subscribe(
      data => {
        this.space_info[drive] = data
      }
      , (error: any) => {
        this.handleError(error);
      });
  }

  public nextPage(){

    let tokenOne = "",tokenDropbox = "",tokenGoogle="";

    if(this.pageTokenDropbox.hasMore=="true"){
      tokenDropbox=this.pageTokenDropbox.token;
    }
    if(this.pageTokenOneDrive.hasMore=="true"){
      tokenOne=this.pageTokenOneDrive.token;
    }
    if(this.pageTokenGoogleDrive.hasMore=="true"){
      tokenGoogle=this.pageTokenGoogleDrive.token;
    }

    if(tokenOne!="" || tokenGoogle!="" || tokenDropbox!=""){
      let dialogRef = this.dialog.open(LoadingComponentComponent, {
        data: {
          msg: "Récupération des fichiers ..."
        }
      });

      this.request.nextPage(this.authService.user.id,this.tab_previous_folder[this.tab_previous_folder.length-1].folder_id, tokenDropbox, tokenOne, tokenGoogle,false).subscribe(
        data => {
          for (let item of data.files) {
            this.fileList.push(item);
          }
          this.tab_fileList = this.divide_file_list(this.fileList);

          this.pageTokenDropbox=data.dropboxToken;
          this.pageTokenOneDrive=data.onedriveToken;
          this.pageTokenGoogleDrive=data.googledriveToken;
          dialogRef.close();
        }
        , (error: any) => {
          this.handleError(error);
        });
    }



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
    this.router.navigate(['/auth']);
  }

  private updateFileList(path: string, idFolder: string, getGoogleDrive: number, getOneDrive: number, getDropbox: number) {


    let dialogRef = this.dialog.open(LoadingComponentComponent, {
      data: {
        msg: "Chargement ..."
      }
    });

    this.request.getFiles(path, idFolder, this.userID, getGoogleDrive, getOneDrive, getDropbox, false).subscribe(
      data => {
        this.fileList = data.files;
        this.tab_fileList = this.divide_file_list(data.files);

        this.pageTokenDropbox=data.dropboxToken;
        this.pageTokenOneDrive=data.onedriveToken;
        this.pageTokenGoogleDrive=data.googledriveToken;
        console.log(data);

        dialogRef.close();
      }
      , (error: any) => {
        this.handleError(error);
      });

  }
}

export class PreviousInfo {

  constructor(public path: string, public folder_id: string, public getGoogledrive: number, public getOnedrive: number,
              public getDropbox: number) {
  }

}

class SpaceInfo {
  quota: string;
  used: string;
  freeSpace: string
}
