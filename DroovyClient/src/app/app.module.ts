import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';


import { AppComponent } from './app.component';
import { AuthService } from './auth.service';
import { HttpClient } from '@angular/common/http';
import { HttpClientModule } from '@angular/common/http';
import { AuthComponent } from './auth/auth.component';
import { AppRoutingModule } from './/app-routing.module';
import { FileManagerComponent } from './file-manager/file-manager.component';
import { AuthDriveComponent } from './auth-drive/auth-drive.component';
import "materialize-css";
import { MaterializeModule } from "angular2-materialize";
import { FormsModule } from '@angular/forms';
import 'rxjs/add/operator/map';
import 'rxjs/Rx';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { RequestService } from './request.service';
import { FileSizePipe } from './file-size.pipe';

const appRoutes: Routes = [
  { path: 'manager', component: FileManagerComponent },
  { path: '', component: AuthComponent },
  { path: '**', component: PageNotFoundComponent }
];



@NgModule({
  declarations: [
    AppComponent,
    AuthComponent,
    FileManagerComponent,
    AuthDriveComponent,
    PageNotFoundComponent,
    FileSizePipe,
  ],
  imports: [
    RouterModule.forRoot(
      appRoutes,
      { enableTracing: true } // <-- debugging purposes only
    ),
    HttpClientModule,
    BrowserModule,
    MaterializeModule,
    FormsModule,
    AppRoutingModule
  ],
  providers: [AuthService,RequestService,HttpClient],
  bootstrap: [AppComponent]
})
export class AppModule { }
