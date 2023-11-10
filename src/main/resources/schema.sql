create table if not exists user
(
    id         identity     not null,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    email      varchar(255) not null,
    password   varchar(255) not null
);
alter table if exists user
    add constraint if not exists uq_email unique (email);

create table if not exists token
(
    id            identity     not null,
    refresh_token varchar(255) not null,
    expired_at    datetime     not null,
    issued_at     datetime     not null,
    user          bigint       not null,
    constraint fk_token_user foreign key (user) references user (id)
);