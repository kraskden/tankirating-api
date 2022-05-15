package me.fizzika.tankirating.repository.online;

import me.fizzika.tankirating.record.online.OnlinePcuRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnlinePcuRepository extends JpaRepository<OnlinePcuRecord, Long> {

}
