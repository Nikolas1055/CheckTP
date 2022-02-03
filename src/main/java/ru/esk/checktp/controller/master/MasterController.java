package ru.esk.checktp.controller.master;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.esk.checktp.entity.CheckMeasure;
import ru.esk.checktp.entity.Checklist;
import ru.esk.checktp.exporter.ExcelExporter;
import ru.esk.checktp.repository.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.List;

@Controller
@RequestMapping("/master")
public class MasterController {
    private final MeasureRepository measureRepository;
    private final CriterionRepository criterionRepository;
    private final ObjectRepository objectRepository;
    private final UserRepository userRepository;
    private final CheckListRepository checkListRepository;
    private final CheckMeasureRepository checkMeasureRepository;
    private final ExcelExporter excelExporter;

    @Autowired
    public MasterController(MeasureRepository measureRepository,
                            CriterionRepository criterionRepository,
                            ObjectRepository objectRepository,
                            UserRepository userRepository,
                            CheckListRepository checkListRepository,
                            CheckMeasureRepository checkMeasureRepository,
                            ExcelExporter excelExporter) {
        this.measureRepository = measureRepository;
        this.criterionRepository = criterionRepository;
        this.objectRepository = objectRepository;
        this.userRepository = userRepository;
        this.checkListRepository = checkListRepository;
        this.checkMeasureRepository = checkMeasureRepository;
        this.excelExporter = excelExporter;
    }

    @GetMapping
    public String masterPageView() {
        return "master/master";
    }

    @GetMapping("/checkList")
    public String checklistPageView(Model model, Long id, Principal principal) {
        model.addAttribute("measurements", measureRepository.findAll());
        model.addAttribute("criteria", criterionRepository.findAll());
        model.addAttribute("checklist", new Checklist());
        model.addAttribute("chosenObject", objectRepository.getById(id));
        model.addAttribute("master", userRepository.findUserByUsername(principal.getName()));
        return "master/checklist";
    }

    @PostMapping("/saveCheckList")
    public String saveChecklist(@RequestParam List<Long> mark,
                                @RequestParam String objectId,
                                Principal principal) {
        double overallGrade = 0;
        for (Long m : mark) {
            overallGrade += measureRepository.getById(m).getGrade();
        }
        Checklist checklist = new Checklist();
        checklist.setMaster(userRepository.findUserByUsername(principal.getName()).orElse(null));
        checklist.setObject(objectRepository.getById(Long.parseLong(objectId)));
        checklist.setDate(new Timestamp(System.currentTimeMillis()));
        checklist.setOverallGrade(Double.parseDouble(new DecimalFormat("0.00")
                .format(overallGrade).replace(",", ".")));
        checkListRepository.save(checklist);
        for (Long m : mark) {
            CheckMeasure checkMeasure = new CheckMeasure();
            checkMeasure.setChecklist(checklist);
            checkMeasure.setMeasure(measureRepository.getById(m));
            checkMeasureRepository.save(checkMeasure);
        }
        //TODO тест отправки письма
//        return "redirect:../sendSimpleEmail";
        return "redirect:/master";
    }

    @GetMapping("/chooseObject")
    public String chooseObjectPageView(Model model) {
        model.addAttribute("objects", objectRepository.findAll());
        return "master/chooseObject";
    }

    @PostMapping("/chooseObject")
    public String chooseObject(@RequestParam Long object, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("id", object);
        return "redirect:../master/checkList";
    }
}
