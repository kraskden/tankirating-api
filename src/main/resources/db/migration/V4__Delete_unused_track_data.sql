delete from track t where t.id not in (select d.track_id from diff d where d.track_id is not null)
                      and t.id not in (select s.track_id from "snapshot" s);
