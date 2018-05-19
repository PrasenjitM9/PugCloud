import {Component, Input, OnInit} from '@angular/core';
import {FileDroovy, PropertiesFileDroovy} from "../request.service";

@Component({
  selector: 'app-file-display',
  templateUrl: './file-display.component.html',
  styleUrls: ['./file-display.component.css']
})
export class FileDisplayComponent implements OnInit {

  @Input() fileDroovy: FileDroovy;

  display_properties = false;


  name_drive: string;
  url_download: string;
  creation_date: string;
  last_update_date: string;

  new_name: string;

  constructor() {

  }


  ngOnInit() {
    this.new_name = this.fileDroovy.name
  }


  propertiesOnedrive() {
    var properties: PropertiesFileDroovy = this.fileDroovy.sourceProperties["Onedrive"];
    this.name_drive = "Onedrive";
    this.url_download = properties.url;
    this.creation_date = properties.creationDate;
    this.last_update_date = properties.lastUpdateDate;

    this.display_properties = true;
  }

  propertiesGoogledrive() {
    var properties: PropertiesFileDroovy = this.fileDroovy.sourceProperties["GoogleDrive"];
    this.name_drive = "Google Drive";
    this.url_download = properties.url;
    this.creation_date = properties.creationDate;
    this.last_update_date = properties.lastUpdateDate;

    this.display_properties = true;

  }

  propertiesDropbox() {
    var properties: PropertiesFileDroovy = this.fileDroovy.sourceProperties["Dropbox"];
    this.name_drive = "Dropbox";
    this.url_download = properties.url;
    this.creation_date = properties.creationDate;
    this.last_update_date = properties.lastUpdateDate;

    this.display_properties = true;
  }


  move() {

  }

  delete() {

  }

  rename() {

  }


}
