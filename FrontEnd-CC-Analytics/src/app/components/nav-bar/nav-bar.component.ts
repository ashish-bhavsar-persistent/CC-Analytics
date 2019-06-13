import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { ThemeService } from 'src/app/core/services/theme.service';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss']
})
export class NavBarComponent implements OnInit {


  isDarkTheme: Observable<boolean>;

  constructor(private themeService: ThemeService, private _authService: AuthService) { }

  toggleDarkTheme(checked: boolean) {
    this.themeService.setDarkTheme(checked);
  }

  logoutUser(){
    this._authService.logout();
  }

  ngOnInit() {
    this.isDarkTheme = this.themeService.isDarkTheme;
  }

}
