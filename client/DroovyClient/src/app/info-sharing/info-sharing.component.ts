import {Component, Input, OnInit} from '@angular/core';
import {Permission} from "../request.service";

@Component({
  selector: 'app-info-sharing',
  templateUrl: './info-sharing.component.html',
  styleUrls: ['./info-sharing.component.css']
})
export class InfoSharingComponent implements OnInit {

  @Input() listPermission: Permission[];

  constructor() {
  }

  ngOnInit() {
  }


}
