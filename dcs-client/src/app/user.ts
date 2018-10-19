import { Participation } from './model';
import { Room } from './model';

export interface User {

    username: string;

    firstName: string;

    lastName: string;

    bio: string;

    visible: boolean;

    lastSeen: Date;

    token: string;

    rooms: Room[];

    participations: Participation[];

}
