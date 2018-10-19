import { Component, OnInit } from '@angular/core';
import { remove } from 'lodash';

import { ChatService } from '../chat.service';
import {
  DeleteRoomRequest,
  CreateRoomRequest,
  LogoutRequest
} from '../requests';
import { Room, User } from '../model';
import { Router } from '@angular/router';
import { Subscriber } from 'rxjs/Subscriber';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {
  rooms: Room[];

  create = false;

  constructor(private service: ChatService, private router: Router) {}

  ngOnInit() {
    console.log(this.service.getUser());
    if (!this.service.getUser()) {
      this.router.navigateByUrl('/login');
    } else {
      // to debug
      this.rooms = [];
      this.rooms.push({
        name: 'Room 1',
        participations: []
      });
    }
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

  logout() {
    this.service
      .logout(new LogoutRequest(this.service.getUser().token))
      .subscribe(_ => {}, console.error, () => {
        this.service.setUser(null);
        this.router.navigateByUrl('/login');
      });
  }

  toggleCreate() {
    this.create = !this.create;
  }
}
