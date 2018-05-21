import {Component, Input, OnInit} from '@angular/core';
import {FileDroovy, PropertiesFileDroovy, RequestService} from "../request.service";
import {AuthService} from "../auth.service";
import {LoadingComponentComponent} from "../loading-component/loading-component.component";
import {MatDialog, MatSnackBar} from "@angular/material";
import {ErrorDialogComponent} from "../error-dialog/error-dialog.component";
import {FileDisplayComponent} from "../file-display/file-display.component";
import {PreviousInfo} from "../file-manager/file-manager.component";

@Component({
  selector: 'app-sharing',
  templateUrl: './sharing.component.html',
  styleUrls: ['./sharing.component.css']
})
export class SharingComponent implements OnInit {


  @Input() selectedDrive : string;
  @Input() selectedRight :string;
  @Input() message :string;
  @Input() mail :string;
  @Input() file : FileDroovy;
  @Input() properties : PropertiesFileDroovy;


  constructor(private snackBar: MatSnackBar,public dialog: MatDialog,private auth : AuthService,private request : RequestService) { }

  ngOnInit() {
  }

  send() {

    if(this.selectedRight== undefined || this.selectedRight==""){
      this.snackBar.open('Choisissez un droit', 'Ok', {
        duration: 3000
      });
    }
    else if(this.selectedDrive=="" || this.selectedDrive == undefined){
      this.snackBar.open('Choisissez un drive', 'Ok', {
        duration: 3000
      });
    }
    else{
      let dialogRef = this.dialog.open(LoadingComponentComponent, {
        data: {
          msg: "Partage en cours ..."
        }
      });


      this.request.share(this.selectedDrive, this.file.type == "FOLDER", this.selectedRight,this.properties.id,this.message,this.auth.user.id,this.mail).subscribe(
        data => {

          dialogRef.close();
          this.snackBar.open('Partager réussi', 'Ok', {
            duration: 3000
          });
        }
        , (error: any) => {
          this.handleError(error);
        });
    }


  }

  handleError(error : any){
    console.log("Erreur : "+error);
    let dialogRef = this.dialog.open(ErrorDialogComponent, {
      data: {
        msg :  "Oups, ....\nUne erreur est survenue, l'action a échoué"
      }
    });
  }

}
