-- Setup periods for
with earliest_snapshots as (
    select s.target_id, min(s.timestamp) as min_ts
    from "snapshot" s
    group by s.target_id
)
update "snapshot" s
set period = greatest(period, 'INIT'::snapshot_period)
from earliest_snapshots e
where s.target_id = e.target_id
  and s.timestamp = e.min_ts;


with earliest_snapshots as (
    select s.target_id, min(s.timestamp) as min_ts
    from "snapshot" s
    group by s.target_id, date_trunc('year',s.timestamp)
)
update "snapshot" s
set period = greatest(period, 'YEAR'::snapshot_period)
from earliest_snapshots e
where s.target_id = e.target_id
  and s.timestamp = e.min_ts;


with earliest_snapshots as (
    select s.target_id, min(s.timestamp) as min_ts
    from "snapshot" s
    group by s.target_id, date_trunc('month',s.timestamp)
)
update "snapshot" s
set period = greatest(period, 'MONTH'::snapshot_period)
from earliest_snapshots e
where s.target_id = e.target_id
  and s.timestamp = e.min_ts;

with earliest_snapshots as (
    select s.target_id, min(s.timestamp) as min_ts
    from "snapshot" s
    group by s.target_id, date_trunc('week',s.timestamp)
)
update "snapshot" s
set period = greatest(period, 'WEEK'::snapshot_period)
from earliest_snapshots e
where s.target_id = e.target_id
  and s.timestamp = e.min_ts;


update snapshot set period = 'DAY'::snapshot_period
	where period is null;