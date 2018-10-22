import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { EventBusService } from './event-bus.service';
import { Room, Participation } from '../model';
import { CreateRoomRequest, DeleteRoomRequest } from '../requests';
import { AuthService } from './auth.service';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private static PREFIX = '/api';

  private static EVENTS = ChatService.PREFIX + '/events';

  private static ROOMS = ChatService.PREFIX + '/rooms';

  private static ROOM_CREATED = 'rooms.created';

  private roomCreated = new Subject<Room>();
  private roomSelected = new Subject<Room>();

  constructor(private http: HttpClient, private eventBus: EventBusService, private auth: AuthService) {}

  selectRoom(room: Room) {
    this.roomSelected.next(room);
  }

  onRoomSelected(): Observable<Room> {
    return this.roomSelected.asObservable();
  }

  createRoom(name: string): Observable<Participation> {
    const user = this.auth.user;
    return this.http.post<Participation>('/api/rooms', new CreateRoomRequest(
      name,
      user.username,
      user.token
    ))
    .pipe(tap(p => this.selectRoom(p.room)));
  }

  deleteRoom(name: string): Observable<Room> {
    const user = this.auth.user;
    const requestBody = new DeleteRoomRequest(name, user.username, user.token);
    return this.http.request<Room>('delete', `${ChatService.ROOMS}`,
      { body: requestBody })
  }

  onRoomCreated(): Observable<Room> {
    return this.roomCreated.asObservable();
  }

}
