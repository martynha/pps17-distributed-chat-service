import { Participation } from './participation';

export interface Message {

    content: string;

    timestamp: Date;

    participation: Participation;

}
