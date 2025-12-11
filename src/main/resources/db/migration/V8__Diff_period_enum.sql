create type diff_period as enum
	('DAY', 'WEEK', 'MONTH', 'YEAR', 'ALL_TIME');

alter table diff
	alter column period SET DATA TYPE diff_period USING period::diff_period;

alter table online_pcu
		alter column period SET DATA TYPE diff_period USING period::diff_period;