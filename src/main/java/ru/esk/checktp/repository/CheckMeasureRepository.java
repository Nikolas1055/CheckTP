package ru.esk.checktp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.esk.checktp.entity.CheckMeasure;
import ru.esk.checktp.entity.Checklist;
import ru.esk.checktp.entity.Measure;

import java.util.List;
import java.util.Optional;

public interface CheckMeasureRepository extends JpaRepository<CheckMeasure, Long> {
    Optional<CheckMeasure> findCheckMeasureByMeasure(Measure measure);

    List<CheckMeasure> findAllByChecklist(Checklist checklist);

    Optional<CheckMeasure> findCheckMeasureByChecklistAndMeasure(Checklist checklist, Measure measure);
}
