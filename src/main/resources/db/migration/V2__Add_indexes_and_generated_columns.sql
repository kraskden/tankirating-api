alter table track
    add column if not exists kd real generated always as (case when deaths = 0 then null else kills::real / deaths end) stored;

alter table track
    add column if not exists kt real generated always as (case when "time" = 0 then null else kills::real/"time" end) stored;

alter table track_activity
    alter column score type bigint;

create index idx_diff_track on diff using hash (track_id);
create index idx_snapshot_track on snapshot using hash (track_id);
create index idx_activity_track on track_activity using hash (track_id);
create index idx_usage_track on track_usage using hash (track_id);

create index idx_snapshot on snapshot (target_id, timestamp);
create index idx_diff on diff (target_id, period, period_start);
