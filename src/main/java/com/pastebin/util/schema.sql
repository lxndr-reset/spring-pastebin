create table if not exists "user"
(
    user_id     serial
        constraint user_pk
            primary key,
    email       varchar(64) not null
        constraint user_email_k
            unique,
    pass_bcrypt varchar(72) not null
);

create table if not exists message
(
    message_id    bigserial
        constraint message_pk
            primary key,
    message_value text                  not null,
    is_deleted    boolean default false not null,
    owner_id      integer
        constraint message_user_user_id_fk
            references "user",
    delete_date   timestamp             not null
);
create table if not exists short_url
(
    url_id             bigserial
        constraint short_urls_pk
            primary key,
    url_value          text not null
        constraint short_url_pk
            unique,
    foreign_message_id bigint
        constraint short_url_pk2
            unique
        constraint short_urls___fk
            references message
            on delete set null
);

create index message_message_id_index
    on message (message_id);

comment on column short_url.url_value is 'A short sequence of characters which will be used as a key in url';

create index user_email_index
    on "user" (email);
