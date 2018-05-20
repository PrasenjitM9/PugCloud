import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {UploadDialog} from "../upload/upload.component";

@Component({
  selector: 'app-file-modification',
  templateUrl: './file-modification.component.html',
  styleUrls: ['./file-modification.component.css']
})
export class FileModificationComponent implements OnInit {

  constructor(private dialogRef: MatDialogRef<UploadDialog>,@Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
  }

}
