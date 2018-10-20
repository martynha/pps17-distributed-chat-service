import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }
<<<<<<< HEAD
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
=======

>>>>>>> 35053086a24dd878652b88fbfe33b03df454c684
}
