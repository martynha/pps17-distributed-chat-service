import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ChatService } from 'src/app/service/chat.service';
import { filter } from 'rxjs/operators';
import {Message} from '../../model';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.scss']
})
export class MessagesComponent implements OnInit {

  roomName: string;
  messages: Message[] = [];

  constructor(
    private chat: ChatService,
    private route: ActivatedRoute,
    private auth: AuthService
  ) {}

  ngOnInit() {
    console.log('Getting user rooms...');

    this.route.params.subscribe(params => {
      this.roomName = params['name'];
    });

    this.chat
    .getMessagesOnRoom(this.roomName)
    .subscribe(messages => this.messages = messages);

    this.chat
      .onMessageSent()
      .pipe(filter(message => this.roomName === message.room.name))
      .subscribe(message => this.addMessage(message));
  }

  addMessage (message: Message) {
      console.log(message.username + "sent: " + message.content);
      this.messages.push(message);
  }

  isUserMessage(message: Message): boolean {
    return message.username === this.auth.user.username;
  }
}
