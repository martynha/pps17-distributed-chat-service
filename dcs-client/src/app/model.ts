export interface Message {

    content: string;

    timestamp: Date;

    participation: Participation;

}

export interface Participation {

    joinDate: Date;

    room: Room;

    user: User;

    messages?: Message[];

}

export interface Room {

    name: string;

    participations?: Participation[];

}

export interface User {

    username: string;

    firstName: string;

    lastName: string;

    bio: string;

    visible: boolean;

    lastSeen: Date;

    token: string;

    rooms?: Room[];

    participaitons?: Participation[];

}
