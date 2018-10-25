import { Component, OnInit } from '@angular/core';
import { ChatService } from '../../service/chat.service';
import { Router } from '@angular/router';
import { remove } from 'lodash';

import { Room } from '../../model';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.scss']
})
export class RoomsComponent implements OnInit {

  showMenu = '';

  rooms: Room[] = [];

  constructor(private chat: ChatService, private router: Router) {
  }

  ngOnInit() {
    this.chat.onRoomCreated().subscribe(room => {
      console.log('Room created!');
      this.rooms.unshift(room);
    });
    this.chat.onRoomDeleted().subscribe(name => remove(this.rooms, room => room.name === name));
  }

  addExpandClass(element) {
    if (element === this.showMenu) {
      this.showMenu = '0';
    } else {
      this.showMenu = element;
    }
  }

  selectRoom(room) {
    this.chat.selectRoom(room);
    this.router.navigate(['/rooms', room.name]);
  }

  deleteRoom(room: Room) {
    console.log(room);
    this.chat.deleteRoom(room.name)
      .subscribe(() => {}, err => console.error(err)
    );
  }

}
