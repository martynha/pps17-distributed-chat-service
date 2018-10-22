import { Component, OnInit } from '@angular/core';
import { ChatService } from "../../service/chat.service";
import { Router } from '@angular/router';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.scss']
})
export class RoomsComponent implements OnInit {
  showMenu = '';

  rooms = [
    {
      name: 'Room1'
    },
    {
      name: 'Room2'
    },
    {
      name: 'Room3'
    },
    {
      name: 'Room4'
    }
  ];

  constructor(private chat: ChatService, private router: Router) {}

  ngOnInit() {
    this.chat.onRoomCreated().subscribe(room => this.rooms.unshift(room));
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

  deleteRoom(room) {
    console.log(room);
    this.chat.deleteRoom(room.name)
      .subscribe(() => {}, err => console.error(err)
    );
  }
}
