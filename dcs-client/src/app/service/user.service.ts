import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AuthService} from "./auth.service";
import {Observable} from "rxjs";
import {User} from "../model";
import {EditProfileRequest} from "../requests";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private static API_PREFIX = '/api';

  private static USERS = UserService.API_PREFIX + '/users';

  constructor(private http: HttpClient, private auth: AuthService) {  }

  editProfile(request: EditProfileRequest): Observable<User> {
    return this.http.put<User>(UserService.USERS + '/' + request.username, request, {
        headers: this.auth.authOptions
      })
  }

  getProfile(username: string): Observable<User> {
    return this.http.get<User>(UserService.USERS + '/' + username, {
      headers: this.auth.authOptions
    })
  }
}
