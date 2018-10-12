-- Database Section
-- ________________ 

use roomservice;

-- Tables Section
-- _____________ 

drop table if exists messages;
create table messages (
     username varchar(20) not null,
     `name` varchar(50) not null,
     `timestamp` timestamp not null,
     content varchar(1024) not null,
     constraint id_message primary key (username, `name`, `timestamp`));

drop table if exists participations;
create table participations (
     username varchar(20) not null,
     `name` varchar(50) not null,
     join_date date not null,
     constraint id_participation primary key (username, `name`));

drop table if exists rooms;
create table rooms (
     `name` varchar(50) not null,
     owner_username varchar(20) not null,
     constraint id_room primary key (`name`));

drop table if exists users;
create table users (
     username varchar(20) not null,
     constraint id_user primary key (username));

-- Constraints Section
-- ___________________ 

alter table messages add constraint FKPM
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> 44c3b974db8f5c7fb7e17912136c51c9fa55c9f9
<<<<<<< HEAD
     foreign key (username, `name`)
     references participations (username, `name`)
     on delete cascade;

alter table participations add constraint FKPR
     foreign key (`name`)
     references rooms (`name`)
     on delete cascade;

alter table participations add constraint FKUP
     foreign key (username)
     references users (username)
     on delete cascade;

alter table rooms add constraint FKOR
     foreign key (owner_username)
     references users (username)
     on delete cascade;
=======
<<<<<<< HEAD
>>>>>>> a7ee139ea4c4690c234aac1160fec15182d5090c
=======
>>>>>>> 44c3b974db8f5c7fb7e17912136c51c9fa55c9f9
     foreign key (user_name, room_name)
     references participations (user_name, room_name);

alter table participations add constraint FKPR
     foreign key (room_name)
     references rooms (name);

alter table participations add constraint FKUP
     foreign key (user_name)
     references users (username);

alter table rooms add constraint FKOR
     foreign key (owner_name)
     references users (username);
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> eb051361c76e4797646752817d00c27040a90d3f
>>>>>>> a7ee139ea4c4690c234aac1160fec15182d5090c
=======
>>>>>>> eb051361c76e4797646752817d00c27040a90d3f
>>>>>>> 44c3b974db8f5c7fb7e17912136c51c9fa55c9f9

-- Index Section
-- _____________ 
