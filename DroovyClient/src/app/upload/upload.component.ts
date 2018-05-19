import {Component, OnInit} from '@angular/core';
import {FileDroovy} from "../request.service";

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css']
})
export class UploadComponent implements OnInit {

  private currentFolder: FileDroovy;

  private currentPath = "";

  constructor() {
  }

  ngOnInit() {
  }

}
