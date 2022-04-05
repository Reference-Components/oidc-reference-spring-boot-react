create table user
(
    user_id      bigint generated by default as identity primary key,
    sub          varchar(255) not null,
    email        varchar(255) not null,
    name         varchar(255) not null
);

create table authority
(
    authority_id      bigint generated by default as identity primary key,
    authority_string  varchar(255) not null,
    user_id           bigint not null,
    foreign key (user_id) references user(user_id)
);