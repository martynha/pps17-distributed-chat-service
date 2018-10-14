-- Database Section
-- ________________ 

use authservice;

-- Tables Section
-- _____________ 

drop table if exists invalid_tokens;
create table invalid_tokens (
     token varchar(1024) not null,
     expiration_date timestamp not null default CURRENT_TIMESTAMP,
     constraint id_invalid_token primary key (token));

drop table if exists users;
create table users (
     username varchar(20) not null,
     `password` char(64) not null,
     constraint id_user primary key (username));

-- Constraints Section
-- ___________________ 

-- Index Section
-- _____________ 
