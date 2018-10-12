export interface LoginRequest {

    username: string;

    password: string;

}

export function LoginRequest(username: string, password: string): LoginRequest {
    return {
        username,
        password
    };
}

export interface RegisterRequest {

    username: string;

    firstName: string;

    lastName: string;

    password: string;

    passwordConfirm: string;

}

export function RegisterRequest(username: string,
                                firstName: string,
                                lastName: string,
                                password: string,
                                passwordConfirm: string): RegisterRequest {
    return {
        username,
        firstName,
        lastName,
        password,
        passwordConfirm
    };
}

export interface LogoutRequest {

    token: string;

}

export function LogoutRequest(token: string): LogoutRequest {
    return {
        token
    };
}
