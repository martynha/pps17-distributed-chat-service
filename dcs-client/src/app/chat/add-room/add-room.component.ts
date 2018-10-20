import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ChatService } from '../../service/chat.service';

import { CreateRoomRequest } from '../../requests';

@Component({
  selector: 'app-add-room',
  templateUrl: './add-room.component.html',
  styleUrls: ['./add-room.component.scss']
})
export class AddRoomComponent implements OnInit {
  name = '';

  constructor(private router: Router, private chat: ChatService) {}

  ngOnInit() {
    this.chat.selectRoom({
      name: ''
    });
  }

  addRoom() {
    this.chat.createRoom(this.name)
      .subscribe(
        p => {
          this.router.navigate(['/rooms', p.room.name]);
        },
        err => console.error(err)
      );
    /*this.parent.createRoom(
      new CreateRoomRequest(
        this.name,
        this.parent.getUser().username,
        this.parent.getUser().token
      ));
    this.name = '';
    const room = {
      name: this.name
    };
    this.chat.selectRoom(room);
    this.router.navigate(['/rooms', room.name]);*/
  }
}
