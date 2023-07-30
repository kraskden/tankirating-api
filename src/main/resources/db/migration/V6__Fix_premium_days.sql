update diff set premium_days = 7 where "period" = 'WEEK' and premium_days >= 8;
update diff set premium_days = 28 where "period" = 'MONTH' and premium_days >= 28
                                    and extract(month from period_start) = 2;
update diff set premium_days = 30 where "period" = 'MONTH' and premium_days >= 31
              and extract(month from period_start) in (4, 6, 9, 11);
update diff set premium_days = 31 where "period" = 'MONTH' and premium_days >= 32;