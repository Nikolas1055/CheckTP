package ru.esk.checktp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.esk.checktp.entity.Position;

import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findPositionByName(String name);
}
