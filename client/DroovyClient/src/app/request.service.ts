import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Rx';
import {UtilitaireService} from "./utilitaire.service";
import {AuthService} from "./auth.service";
import {Router} from "@angular/router";


@Injectable()
export class RequestService {

  private apiUrl = "http://localhost:8080/droovy/request/";

  constructor(private auth :AuthService,private utilitaire : UtilitaireService,private http: HttpClient,private router :Router) { }

  getFiles(path: string, fileId: string, userId: string, getGoogleDrive: number, getOneDrive: number, getDropbox: number, folderOnly : boolean): Observable<Page> {
    var url = this.apiUrl + "list?idUser=" + userId + "&path=" + path + "&idFolder=" + fileId + "&getGoogleDrive=" + getGoogleDrive + "&getOnedrive=" + getOneDrive + "&getDropbox=" + getDropbox + "&folderOnly="+folderOnly;

    return this.http.get<Page>(url, {responseType: 'json'}).catch((err: HttpErrorResponse) => {

      if(err.status == 401){
        if(getDropbox==1){
          this.resetToken("dropbox");
        }
        if(getOneDrive==1){
          this.resetToken("onedrive");
        }
        if(getGoogleDrive==1){
          this.resetToken("googledrive");
        }      }
      else if (err.status == 442){
        this.auth.eraseUser();
      }
      return Observable.empty<any>();
    });
  }

  download(fileName : string, fileId : string, userId : string, drive : string){
    var url = this.apiUrl + "download?idUser=" + userId +  "&idFile=" + fileId + "&drive=" + drive + "&fileName=" + fileName;
    window.location.href = url;

  }

  rename(idUser : string, path : string, idFile : string , drive : string, name : string ){
    var url = this.apiUrl + "rename?idUser=" + idUser + "&path=" + path + "&idFile=" + idFile + "&drive="+drive + "&name="+name;

    return this.http.get<FileDroovy>(url, {responseType: 'json'}).catch((err: HttpErrorResponse) => {
      if(err.status == 401){
        this.resetToken(drive);
      }
      else if (err.status == 442){
        this.auth.eraseUser();
        this.router.navigate(['/manager']);
      }
      return Observable.throw('Error');
    });
  }
  delete(idUser : string, path : string, idFile : string, drive : string ){
    var url = this.apiUrl + "delete?idUser=" + idUser + "&path=" + path + "&idFile=" + idFile + "&drive="+drive;
    return this.http.get(url, {responseType: 'json'}).catch((err: HttpErrorResponse) => {
      if(err.status == 401){
        this.resetToken(drive);
      }
      else if (err.status == 442){
        this.auth.eraseUser();
        this.router.navigate(['/manager']);
      }
      return Observable.throw('Error');
    });
  }

  move(idUser : string, path : string, idFile : string, drive : string, idParent : string, pathParent :string, name : string){
    var url = this.apiUrl + "move?idUser=" + idUser + "&path=" + path + "&idFile=" + idFile + "&drive="+drive+"&idParent="+idParent+"&pathParent="+pathParent+"&name="+name;
    return this.http.get(url, {responseType: 'json'}).catch((err: HttpErrorResponse) => {
      if(err.status == 401){
        this.resetToken(drive);
      }
      else if (err.status == 442){
        this.auth.eraseUser();
        this.router.navigate(['/manager']);
      }
      return Observable.throw('Error');
    });
  }

  upload(formData : FormData){
    var url = this.apiUrl+"upload";
    return this.http.post(url,formData);
  }

  freespace(idUser: string, drive: string) {
    var url = this.apiUrl + "freespace?idUser=" + idUser + "&drive=" + drive;
    return this.http.get(url, {responseType: 'json'}).catch((err: HttpErrorResponse) => {
      if(err.status == 401){
        this.resetToken(drive);
      }
      else if (err.status == 442){
        this.auth.eraseUser();
        this.router.navigate(['/manager']);
      }
      return Observable.throw('Error');
    });
  }


  search(idUser : string,query : string, getDropbox : number, getOnedrive : number,getGoogledrive : number){
    var url = this.apiUrl + "search?idUser=" + idUser + "&query=" + query+"&getDropbox="+getDropbox+"&getOnedrive="+getOnedrive+"&getGoogleDrive="+getGoogledrive;
    return this.http.get<FileDroovy[]>(url, {responseType: 'json'}).catch((err: HttpErrorResponse) => {
      if(err.status == 401){
        if(getDropbox==1){
          this.resetToken("dropbox");
        }
        if(getOnedrive==1){
          this.resetToken("onedrive");
        }
        if(getGoogledrive==1){
          this.resetToken("googledrive");
        }
      }
      else if (err.status == 442){
        this.auth.eraseUser();
        this.router.navigate(['/manager']);
      }
      return Observable.throw('Error');
    });
  }

  createFolder(idUser :string,drive : string, folderName :string, idParent :string, path :string){
    var url = this.apiUrl + "createFolder?idUser=" + idUser + "&drive=" + drive+"&folderName="+folderName+"&path="+path+"&idParent="+idParent;
    return this.http.get(url, {responseType: 'json'}).catch((err: HttpErrorResponse) => {
      if(err.status == 401){
        this.resetToken(drive);
      }
      else if (err.status == 442){
        this.auth.eraseUser();
        this.router.navigate(['/manager']);
      }
      return Observable.throw('Error');
    });
  }
  nextPage(idUser : string,folderId : string,nextPageTokenDropbox : string ,nextPageTokenOnedrive : string, nextPageTokenGoogleDrive : string,folderOnly : boolean){
    var url = this.apiUrl + "nextPage?idUser=" + idUser + "&folderId=" + folderId + "&nextPageTokenOnedrive=" + nextPageTokenOnedrive + "&nextPageTokenDropbox=" + nextPageTokenDropbox + "&nextPageTokenGoogleDrive=" + nextPageTokenGoogleDrive + "&folderOnly="+folderOnly;

    return this.http.get<Page>(url, {responseType: 'json'}).catch((err: HttpErrorResponse) => {
      if(err.status == 401){
        if(nextPageTokenDropbox!=""){
          this.resetToken("dropbox");
        }
        if(nextPageTokenOnedrive!=""){
          this.resetToken("onedrive");
        }
        if(nextPageTokenGoogleDrive!=""){
          this.resetToken("googledrive");
        }
      }
      else if (err.status == 442){
        this.auth.eraseUser();
        this.router.navigate(['/manager']);
      }
      return Observable.throw('Error');
    });
  }

  share(drive : string, folder : boolean, permission : string, fileId : string, message : string, idUser : string, mail : string){
    var url = this.apiUrl + "share?idUser=" + idUser + "&drive=" + drive+"&folder="+folder+"&permission="+permission+"&message="+message+"&idUser="+idUser+"&mail="+mail+"&idFile="+fileId;
    return this.http.get(url, {responseType: 'json'}).catch((err: HttpErrorResponse) => {
      if(err.status == 401){
        this.resetToken(drive);
      }
      else if (err.status == 442){
        this.auth.eraseUser();
        this.router.navigate(['/manager']);
      }
      return Observable.throw('Error');
    });
  }

  getPermission(idUser : string, idFile : string, drive : string ){
    var url = this.apiUrl + "permission?idUser=" + idUser + "&idFile=" + idFile + "&drive="+drive;
    return this.http.get(url, {responseType: 'json'}).catch((err: HttpErrorResponse) => {
      if(err.status == 401){
        this.resetToken(drive);
      }
      else if (err.status == 442){
        this.auth.eraseUser();
        this.router.navigate(['/manager']);
      }
      return Observable.throw('Error');
    });
  }


  resetToken(drive : string){
      if(drive == "googledrive"){
          this.utilitaire.eraseCookie("connectedToGoogleDrive");
          this.auth.user.connectedToGoogleDrive=false;
      }
      else if(drive == "dropbox"){
        this.utilitaire.eraseCookie("connectedToDropbox");
        this.auth.user.connectedToDropbox=false;
      }
      else if(drive == "onedrive"){
        this.utilitaire.eraseCookie("connectedToOneDrive");
        this.auth.user.connectedToOneDrive=false;
      }

  }

}

export interface Page{
  files : FileDroovy[],
  dropboxToken : token,
  onedriveToken : token,
  googledriveToken : token
}
export interface token{
  hasMore :string,
  token : string
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

/*export interface PermissionList <user, role> {
  user: string,
  role: string,
}*/

export interface Permission {
  name: string
  right: string
}
