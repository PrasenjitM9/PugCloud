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
import {FileSizePipe} from './pipes/file-size.pipe';
import {AuthGuard} from './auth-guard.service';
import {UtilitaireService} from "./utilitaire.service";
import {UploadComponent, UploadDialog} from './upload/upload.component';
import {CallbackdriveconnectionComponent} from './callbackdriveconnection/callbackdriveconnection.component';
import {FileDisplayComponent} from './file-display/file-display.component';
import {SearchFolderComponent} from './search-folder/search-folder.component';
import {MatListModule} from '@angular/material/list';
import {MatTabsModule} from '@angular/material/tabs';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatInputModule} from '@angular/material/input';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatChipsModule} from '@angular/material/chips';
import {MatDialogModule, MatFormFieldModule} from '@angular/material';
import {MatSelectModule} from '@angular/material/select';
import {ErrorDialogComponent} from './error-dialog/error-dialog.component';
import {LoadingComponentComponent} from './loading-component/loading-component.component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatGridListModule} from '@angular/material/grid-list';
import {FileModificationComponent} from './file-modification/file-modification.component';
import {SearchBarComponent} from './search-bar/search-bar.component';
import {CreateFolderComponent} from './create-folder/create-folder.component';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {FlexLayoutModule} from "@angular/flex-layout";
import {SearchFilterPipe} from './pipes/search-filter.pipe';
import {SharingComponent} from './sharing/sharing.component';
import {ShortTextPipe} from './pipes/short-text.pipe';
import {InfoSharingComponent} from './info-sharing/info-sharing.component';

const appRoutes: Routes = [
  { path: 'manager', canActivate:[AuthGuard], component: FileManagerComponent },
  { path: 'auth', component: AuthComponent },
  { path: '',   redirectTo: '/auth', pathMatch: 'full' },
  { path: 'connectedToDrive/:drive/:success', component: CallbackdriveconnectionComponent},
  { path: '**', component: PageNotFoundComponent},



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
    UploadDialog,
    ErrorDialogComponent,
    LoadingComponentComponent,
    FileModificationComponent,
    SearchBarComponent,
    CreateFolderComponent,
    SearchFilterPipe,
    ShortTextPipe,
    SharingComponent,
    InfoSharingComponent,
  ],
  imports: [
    RouterModule.forRoot(
      appRoutes,
    ),
    HttpClientModule,
    BrowserModule,
    MaterializeModule,
    FormsModule,
    MatProgressSpinnerModule,
    AppRoutingModule,
    MatButtonModule,
    MatExpansionModule,
    MatListModule,
    MatGridListModule,
    MatSelectModule,
    MatDialogModule,
    MatChipsModule,
    MatFormFieldModule,
    MatSnackBarModule,
    MatInputModule,
    MatCardModule,
    MatTabsModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    FlexLayoutModule
  ],
  entryComponents: [
    UploadDialog,
    LoadingComponentComponent,
    ErrorDialogComponent,
    FileModificationComponent,
    CreateFolderComponent
  ],
  providers: [AuthService, RequestService, HttpClient, AuthGuard, UtilitaireService, ShortTextPipe],
  bootstrap: [AppComponent]
})
export class AppModule { }
