import {Component, Inject, OnInit} from '@angular/core';
import {UploadDialog} from "../upload/upload.component";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";

@Component({
  selector: 'app-loading-component',
  templateUrl: './loading-component.component.html',
  styleUrls: ['./loading-component.component.css']
})
export class LoadingComponentComponent implements OnInit {

  constructor(private dialogRef: MatDialogRef<UploadDialog>,@Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
  }

}
