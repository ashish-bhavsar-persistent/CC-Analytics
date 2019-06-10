import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators, FormControl } from "@angular/forms";
// import { Router } from "@angular/router";
import { ApiService } from "src/app/services/api.service";
import { HttpParams } from "@angular/common/http";
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: "login-component",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.scss"]
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  invalidLogin: boolean = false;
  loginUserData = {
    username:'',
    password:'',
    grant_type:''
  };

  private token : string = '';

  constructor(
    private formBuilder: FormBuilder,
    // private router: Router,
    private apiService: ApiService,
    private authService: AuthService
  ) {}

  email = new FormControl('', [
    Validators.required,
    Validators.email,
  ]);

  password = new FormControl('', [
    Validators.required
  ]);

  loginUser(){
    // this.loginUserData.username = this.email.value;
    // this.loginUserData.password = this.password.value;
    // this.loginUserData.grant_type = "password";
    this.authService.getToken(this.email.value, this.password.value)
    .subscribe(res => {
      let temp = res;
      console.log(temp);
    })
    
  }

  ngOnInit() {
    
  }
}
