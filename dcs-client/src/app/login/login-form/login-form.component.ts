import { Component, OnInit } from '@angular/core';
import { ChatService } from '../../chat.service';
import { LoginRequest } from '../../requests';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css']
})
export class LoginFormComponent {

  request: LoginRequest = new LoginRequest();

  constructor(private service: ChatService, private router: Router) { }

  login() {
    this.service.login(this.request)
    .subscribe(user => {
                      this.service.setUser(user);
                      this.router.navigateByUrl('/');
              },
               error => console.error(error));
  }

}
