create type snapshot_period as enum
	('DAY', 'WEEK', 'MONTH', 'YEAR', 'INIT');

alter table snapshot
  add column period snapshot_period;