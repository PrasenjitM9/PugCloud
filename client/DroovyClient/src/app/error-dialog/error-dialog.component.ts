import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {UploadDialog} from "../upload/upload.component";

@Component({
  selector: 'app-error-dialog',
  templateUrl: './error-dialog.component.html',
  styleUrls: ['./error-dialog.component.css']
})
export class ErrorDialogComponent implements OnInit {

  constructor(private dialogRef: MatDialogRef<UploadDialog>,@Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
  }

  close(){

    this.dialogRef.close();
  }
}
