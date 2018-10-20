import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RegisterRequest } from '../requests';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  request = new RegisterRequest();

  constructor(private router: Router, private auth: AuthService) {}

  ngOnInit() {}

  register() {
    this.auth.register(this.request).subscribe(
      user => {},
      err => {
        console.error(err);
        this.request.password = '';
        this.request.passwordConfirm = '';
      },
      () => this.router.navigateByUrl('/')
    );
  }

}
