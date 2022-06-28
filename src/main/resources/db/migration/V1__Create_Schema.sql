create table if not exists target
(
    id     int4 not null primary key,
    "name" text not null,
    "type" text not null,
    status text not null default 'ACTIVE',

    constraint target_name_type unique ("name", "type")
);

create table if not exists entity
(
    id     int2 not null primary key,
    "name" text not null,
    "type" text not null,

    constraint entity_name_type unique ("name", "type")
);

create table if not exists track
(
    id      int8 not null primary key,
    gold    int4 not null,
    kills   int4 not null,
    deaths  int4 not null,
    cry     int4 not null,
    score   int4 not null,
    "time"  int8 not null,
    kd real generated always as (case when deaths = 0 then null else kills::real / deaths end) stored,
    kt real generated always as (case when "time" = 0 then null else kills::real/"time" end) stored
);

create table if not exists diff
(
    id           int8 not null primary key,
    target_id    int4 not null references target (id) on delete CASCADE,
    period_start timestamp,
    period_end   timestamp,
    "period"     text,
    track_id     int8 references track (id) on delete CASCADE,
    track_start  timestamp,
    track_end    timestamp,
    premium_days int4,
    max_score int4
);

create table if not exists "snapshot"
(
    id          int8      not null primary key,
    target_id   int4 references target (id) on delete CASCADE,
    "timestamp" timestamp null,
    track_id    int8 references track (id) on delete CASCADE,
    has_premium boolean
);

create index if not exists idx_target_timestamp on "snapshot" (target_id, "timestamp");

create table if not exists track_activity
(
    id        int8 not null primary key,
    track_id  int8 not null references track (id) on delete CASCADE,
    entity_id int2 references entity (id),
    score     int8 not null,
    "time"    int8 not null
);

create table if not exists track_usage
(
    id        int8 not null primary key,
    track_id  int8 not null references track (id) on delete CASCADE,
    entity_id int2 references entity (id),
    usages    int4 not null
);

create table if not exists online_snapshot (
    "timestamp" timestamp primary key,
    "online" int4 not null,
    "inbattles" int4 not null
);

create table if not exists online_pcu (
    id int8 primary key,
    "period" text not null,
    period_start timestamp not null,
    period_end timestamp not null,
    track_start timestamp not null,
    track_end timestamp not null,
    online_pcu int4 not null,
    inbattles_pcu int4 -- Inbattles tracking was added into the V1 after online_pcu tracking, so this field may me null
);

create sequence if not exists target_seq increment 1;
create sequence if not exists entity_seq increment 1;

-- Use hibernate sequence pooling optimisation
create sequence if not exists track_seq increment 50;
create sequence if not exists diff_seq increment 50;
create sequence if not exists snapshot_seq increment 50;
create sequence if not exists track_usage_seq increment 50;
create sequence if not exists track_activity_seq increment 50;

create sequence if not exists online_pcu_seq increment 1;

-- Optimize search speed by indexes
create index idx_diff_track on diff using hash (track_id);
create index idx_snapshot_track on snapshot using hash (track_id);
create index idx_activity_track on track_activity using hash (track_id);
create index idx_usage_track on track_usage using hash (track_id);

create index idx_snapshot on snapshot (target_id, timestamp);
create index idx_diff on diff (target_id, period, period_start);
