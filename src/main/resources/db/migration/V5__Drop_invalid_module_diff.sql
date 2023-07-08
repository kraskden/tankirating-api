update track_activity set time = 0, score = 0
where id in (select ta.id
             from track_activity ta
                      left join diff d on d.track_id = ta.track_id
             where (ta."time" < 0 or ta.score < 0)
               and d."period" = 'DAY'
               and d.period_start >= '2023-07-07 00:00:00.000')