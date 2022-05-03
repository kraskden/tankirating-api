# Tanki Rating API V2.0

## Roadmap: 

* Tracking API (fetch, store and process tracking data for accounts)
* Account API Ph.1 (GET + CREATE)
* Online API
* Migration from V1
* Auth for User
* Account API Ph.2 (change nickname, froze, hide, delete, statistics)
* User API (role management: USER, ADMIN, OWNER)

## API: 

* `/account/{name}/snapshot?fmt=[BASE, FULL]`
  * `/latest`
  * `/init`
  * `/2022-05-01` -- later
  * `/[period]/[offset]` -- later
* `/account/{name}/diff`
  * `/custom?from=...&to...` 
  * `/alltime`
  * `/[period]?from=...&to...&page=...&size=...`
* `/group/{name}/diff`
  * `/[period]?from=2022-05-01`

## TODO:

### Tracking: 
* Groups update. Store percents for ITEMS_PLAYED and SUPPLIES_USAGE, don't store any
absolute values, cuz the overflow error may be occups.
Think about synchronized for group values calculations. 
* Track API (snapshots, diffs). Dynamic diff calculations for 2 snapshots
* Snapshot&Diff sanitization. I think DaySnapshotTrackSanitizer is a good first point to start with it

## Dev docs:

### Track domain model:

#### There are 4 kinds of track object representations:

* alternativa (data that returns official Alternativa site ratings.tankionline.com)
* record (represent record in the database)
* dto (view, that returns to the client)
* model (java object, optimized for track computations)

#### Such transitions must be implemented:

* alternativa -> model
* record -> model
* record -> dto 
* model -> record
* model -> dto


