import {Component, OnInit,Input} from '@angular/core';
import {AuthService} from '../auth.service';
import {Router} from "@angular/router";

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css'],
})
export class AuthComponent implements OnInit {

  constructor(private auth: AuthService,private router: Router) {
  }

  ngOnInit() {

  }

  connect(){
    this.auth.connect(this.password,this.name).subscribe(
      data => {
        console.log(data);


        if(data.success == "true") {

          this.createCookie("id",data['id'],1);
         // this.router.navigate(['/manager']);
          this.router.navigateByUrl("/manager");
        }
        else{

        }
      }
    );

  }

  createAccount(){
     this.auth.createAccount(this.password,this.name).subscribe(
      data => {
        console.log(data);

        if(data.success == "true") {
          this.createCookie("id",data['id'],1);
          this.router.navigate(['/manager']);

        }
        else{

        }
      }
    );
  }




  createCookie(name,value,days) {
    if (days) {
      var date = new Date();
      date.setTime(date.getTime()+(days*24*60*60*1000));
      var expires = "; expires="+date.toUTCString();
    }
    else var expires = "";
    document.cookie = name+"="+value+expires+"; path=/";
  }


  @Input() name: string;
  @Input() password: string;
}
