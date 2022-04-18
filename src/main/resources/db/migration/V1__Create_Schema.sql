create table if not exists "group"
(
    id     uuid primary key,
    "name" text unique not null
);

create table if not exists "account"
(
    id       uuid primary key,
    nickname text unique not null
);

create table if not exists "track"
(
    id          uuid primary key,
    target_id   uuid      not null,
    "timestamp" timestamp not null,

    gold        int       not null,
    kills       int       not null,
    deaths      int       not null,
    cry         int       not null,
    score       int       not null,
    "time"      bigint    not null,

    constraint fk_target_account foreign key (target_id) references account (id) on delete cascade,
    constraint fk_target_group foreign key (target_id) references "group" (id) on delete cascade
);

create table if not exists "track_activity"
(
    id       uuid primary key,
    track_id uuid   not null references track (id),
    "name"   text   not null,
    "type"   text   not null,
    "score"  int    not null,
    "time"   bigint not null
);

create table if not exists "track_supply"
(
    id       uuid primary key,
    track_id uuid   not null references track (id),
    "name"   text   not null,
    usages   bigint not null
);

create table if not exists "snapshot"
(
    track_id    uuid primary key references track (id),
    has_premium bool not null
);

create table if not exists "diff_track"
(
    track_id       uuid primary key references track (id),
    "period"       text not null,
    "premium_days" int  not null
);


