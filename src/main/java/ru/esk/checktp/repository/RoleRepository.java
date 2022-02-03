package ru.esk.checktp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.esk.checktp.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
