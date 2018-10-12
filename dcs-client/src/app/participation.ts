import { Room } from './room';
import { User } from './user';
import { Message } from './message';

export interface Participation {

    joinDate: Date;

    room: Room;

    user: User;

    messages: Message[];

}
