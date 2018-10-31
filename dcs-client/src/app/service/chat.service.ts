import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {EventBusService} from './event-bus.service';
import {Participation, Room} from '../model';
import {CreateRoomRequest, DeleteRoomRequest, JoinRoomRequest} from '../requests';
import {AuthService} from './auth.service';
import {map, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private static API_PREFIX = '/api';

  private static EVENTS = ChatService.API_PREFIX + '/events';

  private static ROOMS = ChatService.API_PREFIX + '/rooms';

  private static ROOM_DELETED = 'rooms.deleted';
  private static ROOM_JOINED = 'rooms.joined';

  private roomCreated = new Subject<Room>();
  private roomDeleted = new Subject<string>();
  private roomSelected = new Subject<Room>();
  private roomJoined = new Subject<Participation>();

  constructor(
    private http: HttpClient,
    private eventBus: EventBusService,
    private auth: AuthService
  ) {
    eventBus.connect(ChatService.EVENTS);

    eventBus.registerHandler(ChatService.ROOM_DELETED, (err, msg) => {
      this.roomDeleted.next(msg.body.name);
    });

    eventBus.registerHandler(ChatService.ROOM_JOINED, (err, msg) => {
      this.roomJoined.next(msg.body)
    });
  }

  selectRoom(room: Room) {
    this.roomSelected.next(room);
  }

  onRoomSelected(): Observable<Room> {
    return this.roomSelected.asObservable();
  }

  createRoom(name: string): Observable<string> {
    const user = this.auth.user;
    return this.http
      .post<Room>(ChatService.ROOMS, new CreateRoomRequest(name, user.username), this.auth.authOptions)
      .pipe(tap(room => this.roomCreated.next(room)))
      .pipe(map(room => room.name));
  }

  deleteRoom(name: string): Observable<void> {
    const user = this.auth.user;
    const body = new DeleteRoomRequest(name, user.username);
    return this.http.request<void>('delete', ChatService.ROOMS + "/" + name, {
      body: body,
      headers: this.auth.authOptions.headers
    });
  }

  joinRoom(name: string): Observable<Participation> {
    const user = this.auth.user;
    const body = new JoinRoomRequest(user.username);
    return this.http.post<Participation>(ChatService.ROOMS + "/" + name + "/participations", body,
      this.auth.authOptions);
  }


  onRoomCreated(): Observable<Room> {
    return this.roomCreated
      .asObservable()
      .pipe(tap(room => this.selectRoom(room)));
  }

  onRoomDeleted(): Observable<string> {
    return this.roomDeleted.asObservable();
  }

  onRoomJoined(): Observable<Participation> {
    return this.roomJoined.asObservable();
  }
}
