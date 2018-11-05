import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Participation} from "../../model";
import {ChatService} from "../../service/chat.service";

@Component({
  selector: 'app-room-info',
  templateUrl: './room-info.component.html',
  styleUrls: ['./room-info.component.scss']
})
export class RoomInfoComponent implements OnInit {

  name: string;

  participations: Participation[] = [];

  constructor(private route: ActivatedRoute, private service: ChatService) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.name = params['name'];
      this.getRoomParticipations()
    });


    this.service.onRoomJoined()
      .subscribe(participation => {
        if (participation.room.name === this.name) this.participations.push(participation)
      })
  }

  getRoomParticipations() {
    this.service.getRoomParticipations(this.name)
      .subscribe(participations => this.participations = participations)
  }
}
