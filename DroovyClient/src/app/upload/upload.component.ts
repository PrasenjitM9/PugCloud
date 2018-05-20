import {Component, Inject, Input, OnInit} from '@angular/core';
import { RequestService} from "../request.service";
import {AuthService} from "../auth.service";
import {FileManagerComponent} from "../file-manager/file-manager.component";
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatSnackBar} from '@angular/material';
import {Observable} from "rxjs/Observable";

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css']
})
export class UploadComponent implements OnInit {


  @Input() file_manager: FileManagerComponent;


  constructor(public dialog: MatDialog,public auth : AuthService,private request : RequestService) {
  }

  ngOnInit() {
  }

  upload(){



  }
  openDialog() {
    let dialogRef = this.dialog.open(UploadDialog, {
      data: {
        folder: this.getCurrentFolder(),
        idUser : this.auth.user.id
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      this.refreshList();
    });
  }

  getCurrentFolder (){
    return this.file_manager.tab_previous_folder[this.file_manager.tab_previous_folder.length-1];
  }
  refreshList(){
    this.file_manager.refreshList();
  }


}

@Component({
  selector: 'app-dialog-upload',
  templateUrl: 'upload-dialog.html',
})
export class UploadDialog {

  public file : File;
  @Input() selectedDrive: string;

  constructor(private snackBar: MatSnackBar,private dialogRef: MatDialogRef<UploadDialog>,@Inject(MAT_DIALOG_DATA) public data: any,private request : RequestService) {}

  fileChange(event) {
    let fileList: FileList = event.target.files;
    this.file = fileList[0];

  }

  private createFormData(file) {

    let path = this.data.folder.path;
    if (path == "root"){
      path = "/";
    }
    path+=file.name;

    let formData: FormData = new FormData();
    formData.append('file', file, file.name);
    formData.append('drive', this.selectedDrive);
    formData.append('idUser', this.data.idUser);
    formData.append('parentId', this.data.folder.folder_id);
    formData.append('pathInDrive',path );

    return formData;
  }

  public submit(file) {

    if(this.selectedDrive ==undefined || this.selectedDrive == ""){
      this.snackBar.open('Choisissez un drive', 'Ok', {
        duration: 3000
      });
    }
    else{
      let formData = this.createFormData(file);
      this.request.upload(formData).subscribe(
        data => {
          console.log(data);
          this.dialogRef.close("");
        });
    }

  }
}
