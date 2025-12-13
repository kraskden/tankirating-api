CREATE INDEX idx_snapshot_period_ts on snapshot (period, timestamp);
CREATE INDEX idx_diff_period_start_ts on diff (period, period_start);