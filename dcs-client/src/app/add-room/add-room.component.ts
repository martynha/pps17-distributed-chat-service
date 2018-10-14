import { Component, Inject, OnInit, Input } from '@angular/core';
import { ChatComponent } from '../chat/chat.component';
import { CreateRoomRequest } from '../requests';

@Component({
  selector: 'app-add-room',
  templateUrl: './add-room.component.html',
  styleUrls: ['./add-room.component.css']
})
export class AddRoomComponent implements OnInit {
  @Input()
  name: string = null;

  constructor(@Inject(ChatComponent) private parent: ChatComponent) {}

  ngOnInit() {}

  createRoom() {
    if (this.name == null) {
      console.log('Insert a name to add new room!');
    } else {
      console.log('Create new Room: ' + this.name);
      this.parent.createRoom(
        new CreateRoomRequest(
          this.name,
          this.parent.getUser().username,
          this.parent.getUser().token
        ));
      this.name = '';
    }
  }
}
