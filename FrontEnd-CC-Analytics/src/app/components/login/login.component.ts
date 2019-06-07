import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { ApiService } from "src/app/services/api.service";
import { HttpParams } from "@angular/common/http";

@Component({
  selector: "login-component",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.scss"]
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  invalidLogin: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private apiService: ApiService
  ) {}

  onSubmit() {
    if (this.loginForm.invalid) {
      return;
    }
    const body = new HttpParams()
      .set("username", this.loginForm.controls.username.value)
      .set("password", this.loginForm.controls.password.value)
      .set("grant_type", "password");

    console.log(body.toString());

    this.apiService.login(body.toString()).subscribe(
      data => {
        window.sessionStorage.setItem("token", JSON.stringify(data));
        console.log(window.sessionStorage.getItem("token"));
        this.router.navigate(["/dashboard"]);
      },
      error => {
        alert(error.error.error_description);
      }
    );
  }

  ngOnInit() {
    window.sessionStorage.removeItem("token");
    this.loginForm = this.formBuilder.group({
      username: ["", Validators.compose([Validators.required])],
      password: ["", Validators.required]
    });
  }
}
