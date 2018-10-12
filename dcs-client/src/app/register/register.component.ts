import { Component, OnInit, Input } from '@angular/core';
import { ChatService } from '../chat.service';
import { Router } from '@angular/router';
import { RegisterRequest } from '../requests';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  @Input()
  username = '';

  @Input()
  firstName = '';

  @Input()
  lastName = '';

  @Input()
  password = '';

  @Input()
  passwordConfirm = '';

  constructor(private service: ChatService, private router: Router) { }

  register() {
    this.service.register(RegisterRequest(
      this.username,
      this.firstName,
      this.lastName,
      this.password,
      this.passwordConfirm
    ));
    // .subscribe();
  }

}
