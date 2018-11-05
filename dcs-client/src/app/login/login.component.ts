import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { LoginRequest } from '../requests';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  request = new LoginRequest();

  constructor(private router: Router, private auth: AuthService) {}

  ngOnInit() {}

  login() {
    this.auth.login(this.request).subscribe(
      user => console.log(user),
      err => {
        this.request.password = '';
        console.error(err);
      },
      () => this.router.navigateByUrl('/')
    );
  }

}
