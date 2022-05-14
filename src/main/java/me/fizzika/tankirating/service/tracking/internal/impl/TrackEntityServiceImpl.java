package me.fizzika.tankirating.service.tracking.internal.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.tracking.TrackEntityDTO;
import me.fizzika.tankirating.enums.track.TankiEntityType;
import me.fizzika.tankirating.record.tracking.TrackEntityRecord;
import me.fizzika.tankirating.repository.TrackEntityRepository;
import me.fizzika.tankirating.service.tracking.internal.TrackEntityService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@RequiredArgsConstructor
public class TrackEntityServiceImpl implements TrackEntityService {

    private final TrackEntityRepository entityRepository;

    private Map<Short, TrackEntityDTO> ENTITY_ID_MAP;
    private Map<TankiEntityType, Map<String, Short>> ENTITY_TYPE_MAP;

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    @PostConstruct
    public void init() {
        ENTITY_ID_MAP = new HashMap<>();
        ENTITY_TYPE_MAP = new EnumMap<>(TankiEntityType.class);
        for (TankiEntityType t : TankiEntityType.values()) {
            ENTITY_TYPE_MAP.put(t, new HashMap<>());
        }

        writeLock.lock();
        try {
            entityRepository.findAll()
                    .forEach(r -> {
                        ENTITY_ID_MAP.put(r.getId(), new TrackEntityDTO(r.getId(), r.getName(), r.getType()));
                        ENTITY_TYPE_MAP.get(r.getType()).put(r.getName(), r.getId());
                    });
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public TrackEntityDTO get(Short id) {
        readLock.lock();
        try {
            return ENTITY_ID_MAP.get(id);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Short getId(String name, TankiEntityType type) {
        Map<String, Short> nameMap = ENTITY_TYPE_MAP.get(type);
        readLock.lock();
        if (nameMap.containsKey(name)) {
            try {
                return nameMap.get(name);
            } finally {
                readLock.unlock();
            }
        } else {
            readLock.unlock();
            writeLock.lock();
            try {
                /*
                Let's imagine, that threads A and B search the same name, that is not exists in the map.
                Thread A locks writeLock, after that execution is switching to thread B. Thread B is trying to lock writeLock,
                but thread A already owns it, so it will wait some time...
                At the moment, execution is switching to thread A, and it creates TrackEntityRecord and releases writeLock

                Execution is switched to the Thread B.
                Thread B now locks writeLock. If I don't put this if-check into the code, Thread B will create duplicate record
                (actually SqlException will be throws, thanks to db unique constraint).
                 */
                if (nameMap.containsKey(name)) {
                    return nameMap.get(name);
                }

                TrackEntityRecord rec = new TrackEntityRecord();
                rec.setName(name);
                rec.setType(type);
                TrackEntityRecord saved = entityRepository.save(rec);
                ENTITY_ID_MAP.put(saved.getId(), new TrackEntityDTO(saved.getId(), saved.getName(), saved.getType()));
                nameMap.put(saved.getName(), saved.getId());
                return saved.getId();
            } finally {
                writeLock.unlock();
            }
        }
    }

}
