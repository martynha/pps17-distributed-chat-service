import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

import { LoginRequest, LogoutRequest, RegisterRequest, CreateRoomRequest, DeleteRoomRequest } from './requests';
import { EventBusService } from './event-bus.service';
import { Participation, Room, User } from './model';
import { Subject } from 'rxjs/Subject';
import { flatMap, onErrorResumeNext } from 'rxjs/operators';

@Injectable()
export class ChatService {

  private static PREFIX = '/api';

  private static EVENTS = ChatService.PREFIX + '/events';

  private static LOGIN = ChatService.PREFIX + '/login';
  private static REGISTER = ChatService.PREFIX + '/register';
  private static LOGOUT = ChatService.PREFIX + '/logout';
  private static ROOMS = ChatService.PREFIX + '/room';

  private static ROOM_CREATED = 'rooms.created';

  private user;

  constructor(private client: HttpClient, private eventBus: EventBusService) {
    // eventBus.connect(ChatService.EVENTS);
  }

  login(request: LoginRequest): Observable<User> {
    return this.client.post<User>(ChatService.LOGIN, request);
  }

  register(request: RegisterRequest): Observable<User> {
    return this.client.post<User>(ChatService.REGISTER, request);
  }

  logout(request: LogoutRequest): Observable<void> {
    return this.client.post<void>(ChatService.LOGOUT, request);
  }

  createRoom(request: CreateRoomRequest): Observable<Participation> {
    return this.client.post<Participation>('/api/rooms', request);
  }

  deleteRoom(request: DeleteRoomRequest): Observable<Room> {
    return this.client.post<Room>(ChatService.ROOMS + '/' + request.name, request);
  }

  setUser(user: User) {
    this.user = user;
  }

  getUser():User {
    return this.user;
  }
}
