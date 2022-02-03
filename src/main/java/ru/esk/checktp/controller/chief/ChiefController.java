package ru.esk.checktp.controller.chief;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.esk.checktp.entity.Criterion;
import ru.esk.checktp.entity.Measure;
import ru.esk.checktp.entity.Type;
import ru.esk.checktp.entity.Object;
import ru.esk.checktp.exporter.ExcelExporter;
import ru.esk.checktp.repository.*;
import ru.esk.checktp.service.CustomValidator;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("/chief")
public class ChiefController {
    private final ExcelExporter excelExporter;
    private final ObjectRepository objectRepository;
    private final TypeRepository typeRepository;
    private final CriterionRepository criterionRepository;
    private final MeasureRepository measureRepository;
    private final CheckListRepository checkListRepository;
    private final CustomValidator customValidator;
    private final CheckMeasureRepository checkMeasureRepository;

    @Autowired
    public ChiefController(ObjectRepository objectRepository,
                           TypeRepository typeRepository,
                           CriterionRepository criterionRepository,
                           MeasureRepository measureRepository,
                           CheckListRepository checkListRepository,
                           CustomValidator customValidator,
                           CheckMeasureRepository checkMeasureRepository,
                           ExcelExporter excelExporter) {
        this.objectRepository = objectRepository;
        this.typeRepository = typeRepository;
        this.criterionRepository = criterionRepository;
        this.measureRepository = measureRepository;
        this.checkListRepository = checkListRepository;
        this.customValidator = customValidator;
        this.checkMeasureRepository = checkMeasureRepository;
        this.excelExporter = excelExporter;
    }

    @GetMapping
    public String masterPageView() {
        return "chief/chief";
    }

    @GetMapping("/objects")
    public String objectsPageView(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("objects", objectRepository.findAll(PageRequest.of(page, 10)));
        model.addAttribute("currentPage", page);
        return "chief/objects";
    }

    @GetMapping("/types")
    public String typesPageView(Model model) {
        model.addAttribute("types", typeRepository.findAll());
        return "chief/types";
    }

    @GetMapping("/criteria")
    public String criteriaPageView(Model model) {
        model.addAttribute("criteria", criterionRepository.findAll());
        return "chief/criteria";
    }

    @GetMapping("/measurements")
    public String measuresPageView(Model model) {
        model.addAttribute("measurements", measureRepository.findAll());
        model.addAttribute("criteria", criterionRepository.findAll());
        return "chief/measurements";
    }

    @GetMapping("/newObject")
    public String createNewObject(Model model) {
        model.addAttribute("object", new Object());
        model.addAttribute("objectTypes", typeRepository.findAll());
        return "chief/object";
    }

    @GetMapping("/newType")
    public String createNewType(Model model) {
        model.addAttribute("type", new Type());
        return "chief/type";
    }

    @GetMapping("/newCriterion")
    public String createNewCriterion(Model model) {
        model.addAttribute("criterion", new Criterion());
        return "chief/criterion";
    }

    @GetMapping("/newMeasurement")
    public String createNewMeasurement(Model model) {
        model.addAttribute("measurement", new Measure());
        model.addAttribute("criteria", criterionRepository.findAll());
        return "chief/measurement";
    }

    @GetMapping("/editObject")
    public String editExistObject(Model model, Long id) {
        model.addAttribute("object", objectRepository.getById(id));
        model.addAttribute("objectTypes", typeRepository.findAll());
        return "chief/object";
    }

    @GetMapping("/editType")
    public String editExistType(Model model, Long id) {
        model.addAttribute("type", typeRepository.getById(id));
        return "chief/type";
    }

    @GetMapping("/editCriterion")
    public String editExistCriterion(Model model, Long id) {
        model.addAttribute("criterion", criterionRepository.getById(id));
        return "chief/criterion";
    }

    @GetMapping("/editMeasurement")
    public String editExistMeasurement(Model model, Long id) {
        model.addAttribute("measurement", measureRepository.getById(id));
        model.addAttribute("criteria", criterionRepository.findAll());
        return "chief/measurement";
    }

    @GetMapping("/deleteObject")
    public String deleteExistObject(Long id) {
        if (checkListRepository.findChecklistByObject(objectRepository.findById(id)).orElse(null) == null) {
            objectRepository.deleteById(id);
        }
        //TODO добавить блок else и вывести сообщение что нельзя удалить объект
        return "redirect:/chief/objects";
    }

    @GetMapping("/deleteType")
    public String deleteExistType(Long id) {
        if (objectRepository.findObjectByType(typeRepository.getById(id)).orElse(null) == null) {
            typeRepository.deleteById(id);
        }
        //TODO добавить блок else и вывести сообщение что нельзя удалить тип
        return "redirect:/chief/types";
    }

    @GetMapping("/deleteCriterion")
    public String deleteExistCriterion(Long id) {
        if (measureRepository.findMeasureByCriterion(criterionRepository.getById(id)).orElse(null) == null) {
            criterionRepository.deleteById(id);
        }
        //TODO добавить блок else и вывести сообщение что нельзя удалить должность
        return "redirect:/chief/criteria";
    }

    @GetMapping("/deleteMeasurement")
    public String deleteExistMeasurement(Long id) {
        if (checkMeasureRepository.findCheckMeasureByMeasure(measureRepository.getById(id)).orElse(null) == null) {
            measureRepository.deleteById(id);
        }
        //TODO добавить блок else и вывести сообщение что нельзя удалить должность
        return "redirect:/chief/measurements";
    }

    @PostMapping("/saveObject")
    public String objectSaveUpdate(@Valid Object object,
                                   BindingResult result,
                                   Model model,
                                   @RequestParam Type type,
                                   @RequestParam int number,
                                   @RequestParam String letter) {
        if (customValidator.checkObjectFields(result, object).hasErrors()) {
            model.addAttribute("objectTypes", typeRepository.findAll());
            return "chief/object";
        }
        if (object.getId() != null) {
            object = objectRepository.getById(object.getId());
            object.setType(type);
            object.setNumber(number);
        }
        object.setLetter(letter.toUpperCase());
        objectRepository.save(object);
        return "redirect:/chief/objects";
    }

    @PostMapping("/saveType")
    public String typeSaveUpdate(@Valid Type type,
                                 BindingResult result,
                                 @RequestParam String name) {
        if (customValidator.checkTypeField(result, type).hasErrors()) {
            return "chief/type";
        }
        if (type.getId() != null) {
            type = typeRepository.getById(type.getId());
            type.setName(name);
        }
        typeRepository.save(type);
        return "redirect:/chief/types";
    }

    @PostMapping("/saveCriterion")
    public String criterionSaveUpdate(@Valid Criterion criterion,
                                      BindingResult result,
                                      @RequestParam String name) {
        if (customValidator.checkCriterionField(result, criterion).hasErrors()) {
            return "chief/criterion";
        }
        if (criterion.getId() != null) {
            criterion = criterionRepository.getById(criterion.getId());
            criterion.setName(name);
        }
        criterionRepository.save(criterion);
        return "redirect:/chief/criteria";
    }

    @PostMapping("/saveMeasurement")
    public String measurementSaveUpdate(@Valid Measure measure,
                                        BindingResult result,
                                        @RequestParam String name,
                                        @RequestParam Double grade,
                                        @RequestParam Criterion criterion) {
        if (customValidator.checkMeasureField(result, measure, criterion).hasErrors()) {
            return "chief/measurement";
        }
        if (measure.getId() != null) {
            measure = measureRepository.getById(measure.getId());
            measure.setName(name);
            measure.setGrade(grade);
            measure.setCriterion(criterion);
        }
        measureRepository.save(measure);
        return "redirect:/chief/measurements";
    }

    @GetMapping("/checklists")
    public String checklistsPageView(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("checklists", checkListRepository.findAll(PageRequest.of(page, 10)));
        model.addAttribute("currentPage", page);
        return "chief/checklists";
    }

    @GetMapping("/exportExcel")
    public void exportCheckListToExcel(HttpServletResponse response, Long id) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=checklist_" +
                checkListRepository.getById(id).getObject().getId() + ".xlsx";
        response.setHeader(headerKey, headerValue);
        excelExporter.export(response, checkListRepository.getById(id));
    }
}
