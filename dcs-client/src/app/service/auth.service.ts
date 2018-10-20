import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoginRequest, RegisterRequest, LogoutRequest } from '../requests';
import { Observable } from 'rxjs';
import { User } from '../model';

import { map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private static PREFIX = '/api';

  private static LOGIN = AuthService.PREFIX + '/login';
  private static REGISTER = AuthService.PREFIX + '/register';
  private static LOGOUT = AuthService.PREFIX + '/logout';

  private _user: User;

  get user(): User {
    if (!this.isAuthenticated()) {
      throw new Error('Not logged in');
    }
    return this._user;
  }

  constructor(private http: HttpClient) {}

  isAuthenticated(): boolean {
    return !(!this._user);
  }

  login(request: LoginRequest): Observable<User> {
    return this.http.post<User>(AuthService.LOGIN, request)
      .pipe(tap(user => this._user = user));
  }

  register(request: RegisterRequest): Observable<User> {
    return this.http.post<User>(AuthService.REGISTER, request)
      .pipe(tap(user => this._user = user));
  }

  logout(): Observable<void> {
    return this.http.post<void>(AuthService.LOGOUT, new LogoutRequest(this.user.token))
      .pipe(tap(
        _ => {},
        _ => {},
        () => this._user = undefined));
  }

}
