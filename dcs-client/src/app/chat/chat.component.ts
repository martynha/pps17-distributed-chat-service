import { Component, OnInit } from '@angular/core';
import { remove } from 'lodash';

import { ChatService } from '../chat.service';
import { DeleteRoomRequest, CreateRoomRequest } from '../requests';
import { Room, User} from '../model';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {

  rooms: Room[];

  constructor(private service: ChatService) {
  }

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
    this.service.deleteRoom(new DeleteRoomRequest(room.name, 'TODO: replace with username', 'TODO: replace with token'))
      .subscribe(
        deletedRoom => remove(this.rooms, r => r.name === deletedRoom.name)
      );
  }
  
  createRoom(request: CreateRoomRequest) {
    this.service.createRoom(request).subscribe(
      partecipation => this.rooms.push(partecipation.room)
    );
  }
}
