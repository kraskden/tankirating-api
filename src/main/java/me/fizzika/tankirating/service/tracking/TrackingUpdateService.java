package me.fizzika.tankirating.service.tracking;

import java.util.UUID;

public interface TrackingUpdateService {

    void updateAccount(UUID id, String nickname);

    void updateAll();

}
