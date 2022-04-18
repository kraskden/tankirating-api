## Tanki Rating API V2.0

### Roadmap: 

* Tracking API (fetch, store and process tracking data for accounts)
* Account API Ph.1 (GET + CREATE)
* Online API
* Migration from V1
* Auth for User
* Account API Ph.2 (change nickname, froze, hide, delete, statistics)
* User API (role management: USER, ADMIN, OWNER)

### Tracking design:

**Track**:
* uuid 
* 
* trackType (SNAPSHOT, DIFF)
* target_id (group_id, account_id)
* targetType (GROUP, ACCOUNT)
* timestamp