package ru.esk.checktp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.esk.checktp.entity.Checklist;
import ru.esk.checktp.entity.User;

import java.util.Optional;

public interface CheckListRepository extends JpaRepository<Checklist, Long> {
    Optional<Checklist> findChecklistByObject(Object object);

    Optional<Checklist> findChecklistByMaster(User master);
}
