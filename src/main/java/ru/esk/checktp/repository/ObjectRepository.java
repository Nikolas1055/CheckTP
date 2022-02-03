package ru.esk.checktp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.esk.checktp.entity.Object;
import ru.esk.checktp.entity.Type;

import java.util.Optional;

public interface ObjectRepository extends JpaRepository<Object, Long> {
    Optional<Object> findObjectByTypeAndNumberAndLetter(Type type, int number, String letter);

    Optional<Object> findObjectByType(Type type);
}
