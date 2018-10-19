import { Component, OnInit, Input } from '@angular/core';
import { ChatService } from '../../chat.service';
import { Router } from '@angular/router';
import { RegisterRequest } from '../../requests';

@Component({
  selector: 'app-register-form',
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.css']
})
export class RegisterFormComponent {
  request = new RegisterRequest();

  constructor(private service: ChatService, private router: Router) {}

  register() {
    this.service
      .register(this.request)
      .subscribe(
        user => this.service.setUser(user),
        err => console.error(err),
        () => this.router.navigateByUrl('/'));
  }
}
