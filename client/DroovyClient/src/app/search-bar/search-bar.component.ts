import {Component, Input, OnInit} from '@angular/core';
import {FileDroovy, RequestService} from "../request.service";
import {AuthService} from "../auth.service";
import {FileManagerComponent} from "../file-manager/file-manager.component";

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.css']
})
export class SearchBarComponent implements OnInit {

  @Input() query: string;
  @Input() file_manager: FileManagerComponent;

  public resultResearch : FileDroovy[];

  constructor(private auth : AuthService,private request :  RequestService) { }

  ngOnInit() {
  }

  search(){
      this.request.search(this.auth.user.id,this.query,(this.auth.user.connectedToDropbox ? 1 : 0),(this.auth.user.connectedToOneDrive ? 1 : 0),(this.auth.user.connectedToGoogleDrive ? 1 : 0)).subscribe(
      data => {
        this.resultResearch = data;
        this.file_manager.fileList = this.resultResearch;

        console.log(data);

      });
  }

}
