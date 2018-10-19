import { Component, OnInit } from '@angular/core';
import { ChatService } from '../chat.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(private service: ChatService, private router: Router) {
  }

  ngOnInit(): void {
    if (this.service.getUser()) {
      this.router.navigateByUrl('/');
    }
  }

}
