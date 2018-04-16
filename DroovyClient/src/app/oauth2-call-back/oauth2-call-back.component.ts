import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-oauth2-call-back',
  templateUrl: './oauth2-call-back.component.html',
  styleUrls: ['./oauth2-call-back.component.css']
})
export class Oauth2CallBackComponent implements OnInit {

  constructor(private route: ActivatedRoute,private http: HttpClient) { }

  ngOnInit() {
    let code = this.route.snapshot.queryParams["code"];
    console.log("Code "+code);

/*
    this.http.get("https://www.googleapis.com/oauth2/v4/token?code="+code+"&client_id="+client_id+"&client_secret").subscribe(data => { console.log(data)});*/
  }

}
