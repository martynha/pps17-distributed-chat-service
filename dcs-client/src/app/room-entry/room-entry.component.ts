import {Component, Inject, Input, OnInit} from '@angular/core';
import {Room} from "../room";
import {ChatComponent} from "../chat/chat.component";

@Component({
  selector: 'app-room-entry',
  templateUrl: './room-entry.component.html',
  styleUrls: ['./room-entry.component.css']
})
export class RoomEntryComponent implements OnInit {

  @Input()
  room: Room;

  constructor(@Inject(ChatComponent)
              private parent: ChatComponent) {
  }

  ngOnInit() {
  }

  deleteRoom() {
    console.log("deleteRoom: " + this.room);
    this.parent.deleteRoom(this.room);
  }

}
