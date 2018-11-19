-- Database Section
-- ________________ 

use userservice;

-- Tables Section
-- _____________ 

drop table if exists users;
create table users (
     username varchar(20) not null,
     first_name varchar(50) not null,
     last_name varchar(50) not null,
     bio varchar(1024) not null default '',
     visible char not null default TRUE,
     last_seen timestamp not null default CURRENT_TIMESTAMP,
     constraint id_user primary key (username));

-- Constraints Section
-- ___________________ 

-- Index Section
-- _____________ 
