import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TranslateModule } from '@ngx-translate/core';

import { ChatRoutingModule } from './chat-routing.module';
import { ChatComponent } from './chat.component';
import { MaterialModule } from '../material.module';
import { TopnavComponent } from './topnav/topnav.component';
import { RoomsComponent } from './rooms/rooms.component';
import { RoomComponent } from './room/room.component';
import { BlankComponent } from './blank/blank.component';
import { AddRoomComponent } from './add-room/add-room.component';
import { RoomInfoComponent } from './room-info/room-info.component';
import { FormsModule } from '@angular/forms';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ChatRoutingModule,
    MaterialModule,
    TranslateModule
  ],
  declarations: [
    ChatComponent,
    TopnavComponent,
    RoomsComponent,
    RoomComponent,
    BlankComponent,
    AddRoomComponent,
    RoomInfoComponent
  ],
})
export class ChatModule {}
