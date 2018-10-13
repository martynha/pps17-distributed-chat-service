import {Component, OnInit} from '@angular/core';
import {ChatService} from "../chat.service";
import {RoomDeletionRequest} from "../requests";
import {Room} from "../room";
import remove from "lodash"

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
      name: "Room 1",
      participations: []
    });
  }

  deleteRoom(room: Room) {
    this.service.deleteRoom(RoomDeletionRequest(room.name, "")).subscribe(
      deletedRoom => remove(this.rooms, room => room.name === deletedRoom.name))
  }
}
