import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ChatService } from '../../service/chat.service';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-topnav',
  templateUrl: './topnav.component.html',
  styleUrls: ['./topnav.component.scss']
})
export class TopnavComponent implements OnInit {

  pushRightClass = 'push-right';

  selectedRoom: string;

  constructor(
    private router: Router,
    private auth: AuthService,
    private chat: ChatService,
    private translate: TranslateService
  ) {}

  ngOnInit() {
    this.router.events.subscribe(val => {
      if (
        val instanceof NavigationEnd &&
        window.innerWidth <= 992 &&
        this.isToggled()
      ) {
        this.toggleSidebar();
      }
    });

    this.chat.onRoomSelected()
      .subscribe(room => (this.selectedRoom = room.name));
  }

  isToggled(): boolean {
    const dom: Element = document.querySelector('body');
    return dom.classList.contains(this.pushRightClass);
  }

  toggleSidebar() {
    const dom: Element = document.querySelector('body');
    dom.classList.toggle(this.pushRightClass);
  }

  logout() {
    this.auth.logout().subscribe(
      _ => {},
      err => console.error(err),
      () => this.router.navigateByUrl('/login'));
  }

  changeLang(language: string) {
    this.translate.use(language);
  }

  createRoom() {
    this.router.navigateByUrl('/add-room');
  }

  roomInfo() {
    this.router.navigate(['/rooms', this.selectedRoom, 'info']);
  }

}
