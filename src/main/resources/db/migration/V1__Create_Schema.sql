create table target
(
    id     int4 not null primary key,
    "name" text not null,
    "type" text not null,

    constraint target_name_type unique ("name", "type")
);

create table entity
(
    id     int2 not null primary key,
    "name" text not null,
    "type" text not null,

    constraint entity_name_type unique ("name", "type")
);

create table track
(
    id      int8 not null primary key,
    gold    int4 not null,
    kills   int4 not null,
    deaths  int4 not null,
    cry     int4 not null,
    score   int4 not null,
    "time"  int8 not null
);

create table diff
(
    id           int8 not null primary key,
    target_id    int4 not null references target (id) on delete CASCADE,
    period_start timestamp,
    period_end   timestamp,
    "period"     text,
    track_id     int8 references track (id) on delete CASCADE,
    track_start  timestamp,
    track_end    timestamp,
    premium_days int4
);

create table "snapshot"
(
    id          int8      not null primary key,
    target_id   int4 references target (id) on delete CASCADE,
    "timestamp" timestamp null,
    track_id    int8 references track (id) on delete CASCADE,
    has_premium boolean
);

create index idx_target_timestamp on "snapshot" (target_id, "timestamp");

create table track_activity
(
    id        int8 not null primary key,
    track_id  int8 not null references track (id) on delete CASCADE,
    entity_id int2 references entity (id),
    score     int4 not null,
    "time"    int8 not null
);

create table track_usage
(
    id        int8 not null primary key,
    track_id  int8 not null references track (id) on delete CASCADE,
    entity_id int2 references entity (id),
    usages    int4 not null
);


create sequence target_seq increment 1;
create sequence entity_seq increment 1;

-- Use hibernate sequence pooling optimisation
create sequence track_seq increment 50;
create sequence diff_seq increment 50;
create sequence snapshot_seq increment 50;
create sequence track_usage_seq increment 50;
create sequence track_activity_seq increment 50;
