import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

import {LoginRequest, LogoutRequest, RegisterRequest, RoomDeletionRequest} from './requests';
import {EventBusService} from './event-bus.service';
import {User} from './user';
import {Room} from "./room";

@Injectable()
export class ChatService {

  private static PREFIX = '/api';

  private static LOGIN = ChatService.PREFIX + '/login';
  private static REGISTER = ChatService.PREFIX + '/register';
  private static LOGOUT = ChatService.PREFIX + '/logout';
  private static ROOMS = ChatService.PREFIX + '/room';

  constructor(private client: HttpClient, private eventBus: EventBusService) {
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

  deleteRoom(request: RoomDeletionRequest): Observable<Room> {
    return this.client.post<Room>(ChatService.ROOMS + "/" + request.name, request)
  }
}
