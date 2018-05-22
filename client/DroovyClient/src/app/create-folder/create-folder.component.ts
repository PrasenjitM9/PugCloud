import {Component, Inject, Input, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef, MatSnackBar} from "@angular/material";
import {UploadDialog} from "../upload/upload.component";
import {RequestService} from "../request.service";
import {AuthService} from "../auth.service";

@Component({
  selector: 'app-create-folder',
  templateUrl: './create-folder.component.html',
  styleUrls: ['./create-folder.component.css']
})
export class CreateFolderComponent implements OnInit {

  @Input() selectedDrive : string;
  @Input() folderName : string;


  constructor(public snackBar: MatSnackBar,private auth : AuthService,private request : RequestService,private dialogRef: MatDialogRef<UploadDialog>,@Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
  }
  create(){


    console.log(this.selectedDrive);


    if(this.folderName ==undefined || this.folderName == "" ){
      this.snackBar.open('Donnez un nom à votre dossier', 'Ok', {
        duration: 3000
      });
    }
    else if(this.selectedDrive ==undefined || this.selectedDrive == ""){
      this.snackBar.open('Choisissez un drive', 'Ok', {
        duration: 3000
      });
    }
    else{
      this.request.createFolder(this.auth.user.id,this.selectedDrive,this.folderName,this.data.folder.folder_id,this.data.folder.path).subscribe(
        data => {
          this.dialogRef.close();
        }
        , (error: any) => {

        });
    }

  }

}
