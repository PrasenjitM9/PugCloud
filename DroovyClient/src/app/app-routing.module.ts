import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';

import { Oauth2CallBackComponent } from './oauth2-call-back/oauth2-call-back.component';


const routes: Routes = [
  { path: 'Oauth2CallBack', component: Oauth2CallBackComponent }
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forRoot(routes),
  ],
  exports: [ RouterModule ],
  declarations: []
})
export class AppRoutingModule { }
