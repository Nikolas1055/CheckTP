package ru.esk.checktp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.esk.checktp.entity.Position;
import ru.esk.checktp.entity.Role;
import ru.esk.checktp.entity.Type;
import ru.esk.checktp.entity.User;
import ru.esk.checktp.repository.PositionRepository;
import ru.esk.checktp.repository.RoleRepository;
import ru.esk.checktp.repository.TypeRepository;
import ru.esk.checktp.repository.UserRepository;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CheckTpApplication {
    final TypeRepository typeRepository;
    final RoleRepository roleRepository;
    final UserRepository userRepository;
    final PositionRepository positionRepository;

    @Autowired
    public CheckTpApplication(TypeRepository typeRepository,
                              RoleRepository roleRepository,
                              UserRepository userRepository,
                              PositionRepository positionRepository) {
        this.typeRepository = typeRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.positionRepository = positionRepository;
    }


    public static void main(String[] args) {
        SpringApplication.run(CheckTpApplication.class, args);
    }

    @PostConstruct
    private void init() {
        if (typeRepository.findAll().isEmpty()) {
            typeRepository.save(new Type("ТП"));
            typeRepository.save(new Type("РП"));
        }
        if (roleRepository.findAll().isEmpty()) {
            roleRepository.save(new Role("ROLE_ADMIN"));
            roleRepository.save(new Role("ROLE_CHIEF"));
            roleRepository.save(new Role("ROLE_MASTER"));
        }
        if (positionRepository.findAll().isEmpty()) {
            positionRepository.save(new Position("Администратор"));
        }
        if (userRepository.findAll().isEmpty()) {
            userRepository.save(new User(
                    "Администратор",
                    positionRepository.getById(1L),
                    "admin",
                    new BCryptPasswordEncoder().encode("admin"),
                    true,
                    roleRepository.getById(1L)));
        }
    }
}
