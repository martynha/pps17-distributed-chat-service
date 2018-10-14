import { Component, OnInit } from '@angular/core';
import { remove } from 'lodash';

import { ChatService } from '../chat.service';
import { DeleteRoomRequest, CreateRoomRequest } from '../requests';
import { Room, User } from '../model';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {

  rooms: Room[];

  create = false;

  constructor(private service: ChatService) {}

  ngOnInit() {
    // to debug
    this.rooms = [];
    this.rooms.push({
      name: 'Room 1',
      participations: []
    });
  }

  getUser(): User {
    return this.service.getUser();
  }

  deleteRoom(room: Room) {
    this.service
      .deleteRoom(
        new DeleteRoomRequest(
          room.name,
          this.service.getUser().username,
          this.service.getUser().token
        )
      )
      .subscribe(deletedRoom =>
        remove(this.rooms, r => r.name === deletedRoom.name)
      );
  }
  
  createRoom(request: CreateRoomRequest) {
    this.service.createRoom(request).subscribe(partecipation => {
      this.create = false;
      this.rooms.push(partecipation.room);
    });
  }

  toggleCreate() {
    this.create = !this.create;
  }

}
