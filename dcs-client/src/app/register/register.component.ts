import { Component, OnInit } from '@angular/core';
import { ChatService } from '../chat.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  constructor(private service: ChatService, private router: Router) {
  }

  ngOnInit(): void {
    if (this.service.getUser()) {
      this.router.navigateByUrl('/');
    }
  }

}
