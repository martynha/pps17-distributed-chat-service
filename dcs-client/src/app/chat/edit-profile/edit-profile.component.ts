import {Component, OnInit} from '@angular/core';
import {EditProfileRequest} from '../../requests';
import {Router} from '@angular/router';
import {UserService} from '../../service/user.service';
import {AuthService} from '../../service/auth.service';
import {Location} from '@angular/common';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.scss']
})
export class EditProfileComponent implements OnInit {

  request = new EditProfileRequest();

  constructor(private _location: Location,
              private router: Router,
              private auth: AuthService,
              private userService: UserService) {
  }

  ngOnInit() {
    const user = this.auth.user;
    this.request.username = user.username;

    this.userService.getProfile(user.username).subscribe(
      user => {
        this.request.bio = user.bio;
        this.request.firstName = user.firstName;
        this.request.lastName = user.lastName;
        this.request.visible = user.visible;
      },
      err => console.error(err),
      () => {
      }
    );
  }

  editProfile() {
    this.userService.editProfile(this.request).subscribe(
      () => {
      },
      err => {
        console.error(err);
        alert('Profile edit failed! ' + err.error.error.type);
      },
      () => {
        alert('The user profile has been successfully edited');
        this._location.back()
      }
    );
  }

}
