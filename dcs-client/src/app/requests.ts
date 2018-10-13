export class LoginRequest {

  username: string;

  password: string;

}

export class RegisterRequest {

  username: string;

  firstName: string;

  lastName: string;

  password: string;

  passwordConfirm: string;

}

export class LogoutRequest {

  token: string;

}

export class CreateRoomRequest {

    name: string;

    username: string;

    token: string;

}

export class DeleteRoomRequest {

  constructor(public name: string, public token: string) {
  }

}
