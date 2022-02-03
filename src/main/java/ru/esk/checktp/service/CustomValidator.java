package ru.esk.checktp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.esk.checktp.entity.*;
import ru.esk.checktp.entity.Object;
import ru.esk.checktp.repository.*;

@Service
public class CustomValidator {
    final ObjectRepository objectRepository;
    final TypeRepository typeRepository;
    final PositionRepository positionRepository;
    final UserRepository userRepository;
    final CriterionRepository criterionRepository;
    final MeasureRepository measureRepository;

    @Autowired
    public CustomValidator(ObjectRepository objectRepository,
                           TypeRepository typeRepository,
                           PositionRepository positionRepository,
                           UserRepository masterRepository,
                           CriterionRepository criterionRepository,
                           MeasureRepository measureRepository) {
        this.objectRepository = objectRepository;
        this.typeRepository = typeRepository;
        this.positionRepository = positionRepository;
        this.userRepository = masterRepository;
        this.criterionRepository = criterionRepository;
        this.measureRepository = measureRepository;
    }

    public BindingResult checkObjectFields(BindingResult result, Object object) {
        if (checkObject(objectRepository.findObjectByTypeAndNumberAndLetter(object.getType(), object.getNumber(),
                object.getLetter()).orElse(null), object.getId())) {
            result.addError(new FieldError("object", "number",
                    "Такой объект уже присутствует в базе данных"));
        }
        return result;
    }

    public BindingResult checkTypeField(BindingResult result, Type type) {
        if (checkType(typeRepository.findTypeByName(type.getName()).orElse(null), type.getId())) {
            result.addError(new FieldError("type", "name",
                    "Такой тип уже существует в базе данных"));
        }
        return result;
    }

    public BindingResult checkPositionField(BindingResult result, Position position) {
        if (checkPosition(positionRepository.findPositionByName(position.getName()).orElse(null),
                position.getId())) {
            result.addError(new FieldError("position", "name",
                    "Такая должность уже существует в базе данных"));
        }
        return result;
    }

    public BindingResult checkUserField(BindingResult result, User user) {
        if (checkMaster(userRepository.findMasterByFullName(user.getFullName()).orElse(null),
                user.getId())) {
            result.addError(new FieldError("user", "fullName",
                    "Пользователь с таким ФИО уже существует в базе данных"));
        }
        if (checkMaster(userRepository.findMasterByUsername(user.getUsername()).orElse(null),
                user.getId())) {
            result.addError(new FieldError("user", "username",
                    "Пользователь с таким логином уже существует в базе данных"));
        }
        return result;
    }

    public BindingResult checkCriterionField(BindingResult result, Criterion criterion) {
        if (checkCriterion(criterionRepository.findCriterionByName(criterion.getName()).orElse(null),
                criterion.getId())) {
            result.addError(new FieldError("criterion", "name",
                    "Такой критерий уже существует в базе данных"));
        }
        return result;
    }

    public BindingResult checkMeasureField(BindingResult result, Measure measure, Criterion criterion) {
        if (checkMeasure(measureRepository.findMeasureByNameAndCriterion(measure.getName(), criterion).orElse(null),
                measure.getId())) {
            result.addError(new FieldError("measurement", "name",
                    "Такое измерение уже существует в базе данных"));
        }
        return result;
    }

    private boolean checkObject(Object object, Long id) {
        return (object != null && id == null) || (object != null && !object.getId().equals(id));
    }

    private boolean checkType(Type type, Long id) {
        return (type != null && id == null) || (type != null && !type.getId().equals(id));
    }

    private boolean checkPosition(Position position, Long id) {
        return (position != null && id == null) || (position != null && !position.getId().equals(id));
    }

    private boolean checkMaster(User master, Long id) {
        return (master != null && id == null) || (master != null && !master.getId().equals(id));
    }

    private boolean checkCriterion(Criterion criterion, Long id) {
        return (criterion != null && id == null) || (criterion != null && !criterion.getId().equals(id));
    }

    private boolean checkMeasure(Measure measure, Long id) {
        return (measure != null && id == null) || (measure != null && !measure.getId().equals(id));
    }
}
