package ru.esk.checktp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.esk.checktp.entity.Criterion;

import java.util.Optional;

public interface CriterionRepository extends JpaRepository<Criterion, Long> {
    Optional<Criterion> findCriterionByName(String name);
}
