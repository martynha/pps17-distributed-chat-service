import { Component, OnInit, Inject } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Route } from '@angular/compiler/src/core';
import { ChatService } from '../../service/chat.service';

@Component({
  selector: 'app-room',
  templateUrl: './room.component.html',
  styleUrls: ['./room.component.scss']
})
export class RoomComponent implements OnInit {

  name;
  message = '';

  constructor(private route: ActivatedRoute, private router: Router, private chat: ChatService) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.name = params['name'];
    });
  }

  sendMessage() {
    this.chat.sendMessage(this.message, this.name)
    .subscribe (
      _ => this.message = '',
      err => console.error(err)
    )
  }

}
