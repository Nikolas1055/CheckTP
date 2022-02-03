package ru.esk.checktp.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.esk.checktp.entity.*;
import ru.esk.checktp.repository.*;
import ru.esk.checktp.service.CustomValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {
    final ObjectRepository objectRepository;
    final UserRepository userRepository;
    final PositionRepository positionRepository;
    final CriterionRepository criterionRepository;
    final MeasureRepository measureRepository;
    final TypeRepository typeRepository;
    final CustomValidator customValidator;
    final CheckListRepository checkListRepository;
    final CheckMeasureRepository checkMeasureRepository;
    final RoleRepository roleRepository;

    @Autowired
    public AdminController(ObjectRepository objectRepository,
                           UserRepository masterRepository,
                           PositionRepository positionRepository,
                           CriterionRepository criterionRepository,
                           MeasureRepository measureRepository,
                           TypeRepository typeRepository,
                           CustomValidator customValidator,
                           CheckListRepository checkListRepository,
                           CheckMeasureRepository checkMeasureRepository,
                           RoleRepository roleRepository) {
        this.objectRepository = objectRepository;
        this.userRepository = masterRepository;
        this.positionRepository = positionRepository;
        this.criterionRepository = criterionRepository;
        this.measureRepository = measureRepository;
        this.typeRepository = typeRepository;
        this.customValidator = customValidator;
        this.checkListRepository = checkListRepository;
        this.checkMeasureRepository = checkMeasureRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String adminPageView() {
        return "admin/admin";
    }

    /**
     * Список пользователей
     */
    @GetMapping("/users")
    public String mastersPageView(Model model, Principal principal) {
        model.addAttribute("users", userRepository.findUsersWithoutCurrentAuthAdmin(principal.getName()));
        return "admin/users";
    }

    /**
     * Список должностей
     */
    @GetMapping("/positions")
    public String positionsPageView(Model model) {
        model.addAttribute("positions", positionRepository.findAll());
        return "admin/positions";
    }


    @GetMapping("/newPosition")
    public String createNewPosition(Model model) {
        model.addAttribute("position", new Position());
        return "admin/position";
    }

    @GetMapping("/newUser")
    public String createNewMaster(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/user";
    }


    /**
     * Изменить должность
     */
    @GetMapping("/editPosition")
    public String editExistPosition(Model model, Long id) {
        model.addAttribute("position", positionRepository.getById(id));
        return "admin/position";
    }

    /**
     * Изменить пользователя
     */
    @GetMapping("/editUser")
    public String editExistMaster(Model model, Long id) {
        model.addAttribute("user", userRepository.getById(id));
        model.addAttribute("positions", positionRepository.findAll());
        return "admin/user";
    }


    /**
     * Удалить должность
     */
    @GetMapping("/deletePosition")
    public String deleteExistPosition(Long id) {
        if (userRepository.findMasterByPosition(positionRepository.getById(id)).orElse(null) == null) {
            positionRepository.deleteById(id);
        }
        //TODO добавить блок else и вывести сообщение что нельзя удалить должность
        return "redirect:/admin/positions";
    }

    /**
     * Удалить пользователя
     */
    @GetMapping("/deleteUser")
    public String deleteExistMaster(Long id) {
        if (checkListRepository.findChecklistByMaster(userRepository.getById(id)).orElse(null) == null) {
            userRepository.deleteById(id);
        }
        //TODO добавить блок else и вывести сообщение что нельзя удалить мастера
        return "redirect:/admin/users";
    }

    /**
     * Сохранить должность
     */
    @PostMapping("/savePosition")
    public String positionSaveUpdate(@Valid Position position,
                                     BindingResult result,
                                     @RequestParam String name) {
        if (customValidator.checkPositionField(result, position).hasErrors()) {
            return "admin/position";
        }
        if (position.getId() != null) {
            position = positionRepository.getById(position.getId());
            position.setName(name);
        }
        positionRepository.save(position);
        return "redirect:/admin/positions";
    }

    /**
     * Сохранить пользователя
     */
    @PostMapping("/saveUser")
    public String masterSaveUpdate(@Valid User user,
                                   Model model,
                                   BindingResult result,
                                   @RequestParam String fullName,
                                   @RequestParam Position position,
                                   @RequestParam String username,
                                   @RequestParam Role role,
                                   @RequestParam String password) {
        if (customValidator.checkUserField(result, user).hasErrors()) {
            model.addAttribute("positions", positionRepository.findAll());
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/user";
        }
        if (user.getId() != null) {
            user = userRepository.getById(user.getId());
            user.setFullName(fullName);
            user.setPosition(position);
            user.setUsername(username);
            user.setRole(role);
        } else {
            user.setPassword(new BCryptPasswordEncoder().encode(password));
            user.setEnabled(true);
        }
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    /**
     * Установить новый пароль пользователю
     */
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String id,
                                 @RequestParam String password,
                                 HttpServletRequest request) {
        User user = userRepository.getById(Long.parseLong(id));
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        userRepository.save(user);
        return "redirect:" + request.getHeader("Referer");
    }

    /**
     * Заблокировать/разблокировать пользователя
     */
    @GetMapping("/block")
    public String blockAction(Long id, HttpServletRequest request) {
        User user = userRepository.getById(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        return "redirect:" + request.getHeader("Referer");
    }

    /**
     * Передать в модальное окно Id
     */
    @GetMapping("/find")
    @ResponseBody
    public Long findUser(Long id) {
        return id;
    }
}
