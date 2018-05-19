import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Rx';


@Injectable()
export class RequestService {

  private apiUrl = "http://localhost:8080/droovy/";

  constructor(private http: HttpClient) { }

  getFiles(path: string, fileId: string, userId: string, getGoogleDrive: number, getOneDrive: number, getDropbox: number): Observable<FileDroovy[]> {
    var url = this.apiUrl + "request/list?idUser=" + userId + "&path=" + path + "&idFolder=" + fileId + "&getGoogleDrive=" + getGoogleDrive + "&getOnedrive=" + getOneDrive + "&getDropbox=" + getDropbox;

    return this.http.get<FileDroovy[]>(url, {responseType: 'json'})
  }



}

export interface FileDroovy {

  name: string
  taille: string
  type: string
  sourceProperties: PropertiesFileDroovy[]

}

export interface PropertiesFileDroovy {
  id: string,
  url: string,
  creationDate: string,
  lastUpdateDate: string,
  contentHash: string
}
