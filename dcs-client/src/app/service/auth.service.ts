import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {LoginRequest, LogoutRequest, RegisterRequest} from '../requests';
import {Observable} from 'rxjs';
import {User} from '../model';

import {tap} from 'rxjs/operators';

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

  get authOptions(): HttpHeaders {
    return new HttpHeaders({ Authorization: this.user.token });
  }

  constructor(private http: HttpClient) {
  }

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
    return this.http.request<void>('delete', AuthService.LOGOUT, {
      body: new LogoutRequest(this.user.username),
      headers: this.authOptions
    }).pipe(tap(
        _ => {},
        _ => {},
        () => this._user = undefined));
  }

}
