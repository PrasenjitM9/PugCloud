import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {UploadDialog} from "../upload/upload.component";
import {Router} from '@angular/router';

@Component({
  selector: 'app-file-modification',
  templateUrl: './file-modification.component.html',
  styleUrls: ['./file-modification.component.css']
})
export class FileModificationComponent implements OnInit {

  constructor(private dialogRef: MatDialogRef<UploadDialog>,@Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
  }

  onRename(){
    this.data.fileDisplay.rename();
    }
  onDelete(){
    this.data.fileDisplay.delete();
  }
  onDownload(){
    this.data.fileDisplay.download();
  }
}

