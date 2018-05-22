import {Component, Input, OnInit} from '@angular/core';
import {FileDroovy, Permission, PropertiesFileDroovy, RequestService} from '../request.service';
import {AuthService} from "../auth.service";
import {FileManagerComponent} from "../file-manager/file-manager.component";
import {MatDialog, MatDialogRef} from "@angular/material";
import {ErrorDialogComponent} from "../error-dialog/error-dialog.component";
import {FileModificationComponent} from "../file-modification/file-modification.component";

@Component({
  selector: 'app-file-display',
  templateUrl: './file-display.component.html',
  styleUrls: ['./file-display.component.css']
})
export class FileDisplayComponent implements OnInit {

  @Input() fileDroovy: FileDroovy;
  @Input() file_manager: FileManagerComponent;

  display_properties = false;
  choosenDrive: string;

  name_drive: string;
  url_download: string;
  creation_date: string;
  last_update_date: string;

  public listPermission: Permission[] = [];

  private dialogRef: MatDialogRef<FileModificationComponent>;

  new_name: string;
  properties: PropertiesFileDroovy;

  constructor(public dialog: MatDialog, private request: RequestService,private auth : AuthService) {

  }


  ngOnInit() {
    this.new_name = this.fileDroovy.name
  }


  propertiesOnedrive() {
    if (this.display_properties === false) {
      this.properties = this.fileDroovy.sourceProperties["OneDrive"];
      this.name_drive = "Onedrive";
      this.url_download = this.properties.url;
      this.creation_date = this.properties.creationDate;
      this.last_update_date = this.properties.lastUpdateDate;

      this.display_properties = true;
      this.choosenDrive = "onedrive";
      this.getPermission();
      this.loadDialog();

    } else {
      this.display_properties = false;
    }

  }

  propertiesGoogledrive() {
    if (this.display_properties === false) {
      this.properties = this.fileDroovy.sourceProperties["GoogleDrive"];
      this.name_drive = "Google Drive";
      this.url_download = this.properties.url;
      this.creation_date = this.properties.creationDate;
      this.last_update_date = this.properties.lastUpdateDate;

      this.display_properties = true;
      this.choosenDrive = "googledrive";
      this.getPermission();
      this.loadDialog();

    } else {
      this.display_properties = false;
    }
  }

  propertiesDropbox() {
    if (this.display_properties === false) {
      this.properties = this.fileDroovy.sourceProperties["Dropbox"];
      this.name_drive = "Dropbox";
      this.url_download = this.properties.url;
      this.creation_date = this.properties.creationDate;
      this.last_update_date = this.properties.lastUpdateDate;

      this.display_properties = true;
      this.choosenDrive = "dropbox";

      if (this.fileDroovy.type == "FILE") {
        this.getPermission();
      }
      this.loadDialog();

    } else {
      this.display_properties = false;
    }
  }

  loadDialog(){
    this.dialogRef = this.dialog.open(FileModificationComponent, {
      data: {
        fileDisplay : this
      }
    });
  }

  move(choice : any ) {

    this.request.move(this.auth.user.id,choice.pathParent+"/"+this.fileDroovy.name,this.properties.id,this.choosenDrive,choice.idParent,"/"+this.fileDroovy.name,this.fileDroovy.name).subscribe(
      data => {
        console.log(data);
      }
      , (error: any) => {
        this.handleError(error);
      });
    this.refreshList();
  }
  delete() {
    this.request.delete(this.auth.user.id,"/"+this.fileDroovy.name,this.properties.id,this.choosenDrive).subscribe(
      data => {
        console.log(data);
      }
      , (error: any) => {
        this.handleError(error);
      });

    this.refreshList();
    this.file_manager.updateFreespace()
  }
  rename() {
    this.request.rename(this.auth.user.id,"/"+this.fileDroovy.name,this.properties.id,this.choosenDrive,this.new_name).subscribe(
      data => {
        console.log(data);
      }
      , (error: any) => {
        this.handleError(error);
      });
    this.refreshList();
  }

  download(){
    this.request.download(this.fileDroovy.name,this.properties.id, this.auth.user.id,this.choosenDrive);
  }

  refreshList(){
    this.dialogRef.close();
    this.file_manager.refreshList()
  }

  handleError(error : any){
    console.log(error);
    let dialogRef = this.dialog.open(ErrorDialogComponent, {
      data: {
        msg :  "Oups, ....\nUne erreur est survenue, l'action a échoué"
      }
    });
  }

  openFolder() {

    var path: string;
    var length_path = this.file_manager.tab_previous_folder.length;

    if (length_path > 1) {
      path = this.file_manager.tab_previous_folder[length_path - 1].path + this.fileDroovy.name + "/";
    }
    else {
      path = "/" + this.fileDroovy.name + "/"
    }

    var idFolder = "";

    var getGoogleDrive = 0;
    var getOneDrive = 0;
    var getDropbox = 0;

    if (this.fileDroovy.sourceProperties["Dropbox"]) {
      getDropbox = 1
    }
    if (this.fileDroovy.sourceProperties["OneDrive"]) {
      getOneDrive = 1
    }

    if (this.fileDroovy.sourceProperties["GoogleDrive"]) {
      idFolder = this.fileDroovy.sourceProperties["GoogleDrive"].id;
      getGoogleDrive = 1
    }

    this.file_manager.navigate(path, idFolder, getGoogleDrive, getOneDrive, getDropbox)
  }

  getPermission() {
    this.request.getPermission(this.auth.user.id, this.properties.id, this.choosenDrive).subscribe(
      data => {
        this.listPermission = data;
        console.log(this.listPermission);
      }
      , (error: any) => {
        this.handleError(error);
      });
  }
}

