create database java22_diaocha default charset utf8mb4;

use java22_diaocha;

create table users (
    uid int primary key auto_increment,
    username varchar(30) not null unique,
    password char(60) not null
);

create table questions (
    qid int primary key auto_increment,
    uid int not null,
    question varchar(200) not null,
    options text not null
);

create table surveys (
    sid int primary key auto_increment,
    uid int not null,
    title varchar(200) not null,
    brief varchar(400) not null
);

create table relations (
    rid int primary key auto_increment,
    sid int not null,
    qid int not null
);

create table activities (
    aid int primary key auto_increment,
    uid int not null,
    sid int not null,
    started_at datetime not null,
    ended_at datetime not null
);

create table results (
    reid int primary key auto_increment,
    nickname varchar(20) not null,
    phone varchar(20) not null,
    answer text not null
);