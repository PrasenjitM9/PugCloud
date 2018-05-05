import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {Observable} from 'rxjs/Rx';


@Injectable()
export class RequestService {

  private apiUrl = "http://localhost:8080/droovy/";

  constructor(private http: HttpClient) { }

  getFiles(path : string,fileId :string,userId : string) : Observable<FileDroovy[]>{
    var url = this.apiUrl+"request/list?idUser="+userId+"&path="+path+"&idFolder="+fileId;
    return this.http.get<FileDroovy[]>(url, {responseType: 'json'});
  }

}

export interface FileDroovy {

  name : string,
  id : string,
  url : string,
  source : string[],
  creationDate : string,
  lastUpdateDate : string,
  taille : string,
  type : string,
  contentHash : string
}
