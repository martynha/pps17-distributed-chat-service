export interface Message {

    content: string;

    timestamp: Date;

    username: string;

    room: Room;

}

export interface Participation {

    joinDate: Date;

    room: Room;

    username: string;

}

export interface Room {

    name: string;

}

export interface User {

    username: string;

    firstName: string;

    lastName: string;

    bio: string;

    visible: boolean;

    lastSeen: Date;

    token: string;

}
