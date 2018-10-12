import { Participation } from './participation';
import { Room } from './room';

export interface User {

    username: string;

    firstName: string;

    lastName: string;

    bio: string;

    visible: boolean;

    lastSeen: Date;

    rooms: Room[];

    participaitons: Participation[];

}
