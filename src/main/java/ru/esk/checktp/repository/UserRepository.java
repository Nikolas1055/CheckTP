package ru.esk.checktp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.esk.checktp.entity.User;
import ru.esk.checktp.entity.Position;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findMasterByPosition(Position position);

    Optional<User> findMasterByFullName(String fullName);

    Optional<User> findMasterByUsername(String username);

    Optional<User> findUserByUsername(String username);

    @Query(value = "select u from User u where u.username not like ?1")
    List<User> findUsersWithoutCurrentAuthAdmin(String username);
}
