import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';


import {AppComponent} from './app.component';
import {AuthService} from './auth.service';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {AuthComponent} from './auth/auth.component';
import {AppRoutingModule} from './/app-routing.module';
import {FileManagerComponent} from './file-manager/file-manager.component';
import {AuthDriveComponent} from './auth-drive/auth-drive.component';
import "materialize-css";
import {MaterializeModule} from "angular2-materialize";
import {FormsModule} from '@angular/forms';
import 'rxjs/add/operator/map';
import 'rxjs/Rx';
import {RouterModule, Routes} from '@angular/router';
import {PageNotFoundComponent} from './page-not-found/page-not-found.component';
import {RequestService} from './request.service';
import {FileSizePipe} from './file-size.pipe';
import {AuthGuard} from './auth-guard.service';
import {UtilitaireService} from "./utilitaire.service";
import {UploadComponent} from './upload/upload.component';
import {CallbackdriveconnectionComponent} from './callbackdriveconnection/callbackdriveconnection.component';
import {FileDisplayComponent} from './file-display/file-display.component';
import { SearchFolderComponent } from './search-folder/search-folder.component';
import {MatListModule} from '@angular/material/list';
import {MatTabsModule} from '@angular/material/tabs';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatInputModule} from '@angular/material/input';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatChipsModule} from '@angular/material/chips';

const appRoutes: Routes = [
  { path: 'manager', canActivate:[AuthGuard], component: FileManagerComponent },
  { path: 'auth', component: AuthComponent },
  { path: '', component: AuthComponent },
  {path: 'connectedToDrive/:drive/:success', component: CallbackdriveconnectionComponent},
  {path: '**', component: PageNotFoundComponent},



];



@NgModule({
  declarations: [
    AppComponent,
    AuthComponent,
    FileManagerComponent,
    AuthDriveComponent,
    PageNotFoundComponent,
    FileSizePipe,
    FileDisplayComponent,
    UploadComponent,
    CallbackdriveconnectionComponent,
    SearchFolderComponent,
  ],
  imports: [
    RouterModule.forRoot(
      appRoutes,
    ),
    HttpClientModule,
    BrowserModule,
    MaterializeModule,
    FormsModule,
    AppRoutingModule,
    MatButtonModule,
    MatExpansionModule,
    MatListModule,
    MatChipsModule,
    MatInputModule,
    MatCardModule,
    MatTabsModule,
    BrowserAnimationsModule,
    MatToolbarModule,
  ],
  providers: [AuthService, RequestService, HttpClient, AuthGuard, UtilitaireService],
  bootstrap: [AppComponent]
})
export class AppModule { }
