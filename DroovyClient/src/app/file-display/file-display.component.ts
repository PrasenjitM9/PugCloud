import {Component, Input, OnInit} from '@angular/core';
import {FileDroovy, PropertiesFileDroovy, RequestService} from "../request.service";
import {AuthService} from "../auth.service";
import {FileManagerComponent} from "../file-manager/file-manager.component";

@Component({
  selector: 'app-file-display',
  templateUrl: './file-display.component.html',
  styleUrls: ['./file-display.component.css']
})
export class FileDisplayComponent implements OnInit {

  @Input() fileDroovy: FileDroovy;
  @Input() file_manager: FileManagerComponent;

  display_properties = false;
  choosenDrive : string;

  name_drive: string;
  url_download: string;
  creation_date: string;
  last_update_date: string;

  new_name: string;
  properties: PropertiesFileDroovy;

  constructor(private request : RequestService,private auth : AuthService) {

  }


  ngOnInit() {
    this.new_name = this.fileDroovy.name
  }


  propertiesOnedrive() {
    this.properties = this.fileDroovy.sourceProperties["Onedrive"];
    this.name_drive = "Onedrive";
    this.url_download = this.properties.url;
    this.creation_date = this.properties.creationDate;
    this.last_update_date = this.properties.lastUpdateDate;

    this.display_properties = true;
    this.choosenDrive = "onedrive";

  }

  propertiesGoogledrive() {
    this.properties = this.fileDroovy.sourceProperties["GoogleDrive"];
    this.name_drive = "Google Drive";
    this.url_download = this.properties.url;
    this.creation_date = this.properties.creationDate;
    this.last_update_date = this.properties.lastUpdateDate;

    this.display_properties = true;
    this.choosenDrive = "googledrive";

  }

  propertiesDropbox() {
    this.properties = this.fileDroovy.sourceProperties["Dropbox"];
    this.name_drive = "Dropbox";
    this.url_download = this.properties.url;
    this.creation_date = this.properties.creationDate;
    this.last_update_date = this.properties.lastUpdateDate;

    this.display_properties = true;
    this.choosenDrive = "dropbox";
  }

  move(choice : any ) {

      this.request.move(this.auth.user.id,choice.pathParent+"/"+this.fileDroovy.name,this.properties.id,this.choosenDrive,choice.idParent,"/"+this.fileDroovy.name,this.fileDroovy.name).subscribe(
      data => {
        console.log(data);
      });
    this.refreshList();
  }
  delete() {
    this.request.delete(this.auth.user.id,"/"+this.fileDroovy.name,this.properties.id,this.choosenDrive).subscribe(
      data => {
        console.log(data);
      });
    this.refreshList();
  }
  rename() {
    this.request.rename(this.auth.user.id,"/"+this.fileDroovy.name,this.properties.id,this.choosenDrive,this.new_name).subscribe(
      data => {
        console.log(data);
      });
    this.refreshList();
  }

  download(){
    this.request.download(this.fileDroovy.name,this.properties.id, this.auth.user.id,this.choosenDrive);
  }

  refreshList(){
    this.file_manager.refreshList()
  }

  openFolder() {
    var path: string;

    var getGoogleDrive = 0;
    var getOneDrive = 0;
    var getDropbox = 0;

    if (this.fileDroovy.sourceProperties["Dropbox"]) {
      path = this.fileDroovy.sourceProperties["Dropbox"].path;
      getDropbox = 1
    }
    if (this.fileDroovy.sourceProperties["Onecloud"]) {
      path = this.fileDroovy.sourceProperties["Onecloud"].path;
      getOneDrive = 1
    }

    if (this.fileDroovy.sourceProperties["GoogleDrive"]) {
      getGoogleDrive = 1
    }

    this.file_manager.navigate(path, this.fileDroovy.sourceProperties["GoogleDrive"].id, getGoogleDrive, getOneDrive, getDropbox)
  }
}

