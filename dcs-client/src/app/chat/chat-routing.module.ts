import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ChatComponent } from './chat.component';
import { RoomComponent } from './room/room.component';
import { BlankComponent } from './blank/blank.component';
import { AddRoomComponent } from './add-room/add-room.component';
import { RoomInfoComponent } from './room-info/room-info.component';

const routes: Routes = [{
    path: '',
    component: ChatComponent,
    children: [
      {
        path: '',
        component: BlankComponent
      },
      {
        path: 'add-room',
        component: AddRoomComponent
      },
      {
        path: 'rooms/:name',
        component: RoomComponent
      },
      {
        path: 'rooms/:name/info',
        component: RoomInfoComponent
      }
    ]
  }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ChatRoutingModule { }
