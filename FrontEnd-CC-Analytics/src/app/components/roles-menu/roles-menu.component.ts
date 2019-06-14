import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'roles-menu',
  templateUrl: './roles-menu.component.html',
  styleUrls: ['./roles-menu.component.scss']
})
export class RolesMenuComponent implements OnInit {

  @Input() roles: any;

  constructor() { }

  ngOnInit() {
  }

}
