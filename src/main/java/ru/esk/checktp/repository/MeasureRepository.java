package ru.esk.checktp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.esk.checktp.entity.Criterion;
import ru.esk.checktp.entity.Measure;

import java.util.Optional;

public interface MeasureRepository extends JpaRepository<Measure, Long> {
    Optional<Measure> findMeasureByCriterion(Criterion criterion);

    Optional<Measure> findMeasureByNameAndCriterion(String name, Criterion criterion);

    @Query(value = "select sum(m.grade) from Measure m where m.criterion = ?1")
    double sumGradeMeasurementsByCriterion(Criterion criterion);
}
