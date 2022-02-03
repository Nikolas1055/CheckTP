package ru.esk.checktp.exporter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.esk.checktp.entity.Checklist;
import ru.esk.checktp.entity.Criterion;
import ru.esk.checktp.entity.Measure;
import ru.esk.checktp.repository.CheckMeasureRepository;
import ru.esk.checktp.repository.CriterionRepository;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelExporter {
    private final CriterionRepository criterionRepository;
    private final CheckMeasureRepository checkMeasureRepository;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private int rowCount = 0;
    private Checklist checklist;

    @Autowired
    public ExcelExporter(CriterionRepository criterionRepository,
                         CheckMeasureRepository checkMeasureRepository) {
        this.criterionRepository = criterionRepository;
        this.checkMeasureRepository = checkMeasureRepository;
    }

    private CellStyle cellStyle(XSSFWorkbook wb, boolean bold, boolean italic, boolean wrap, int height,
                                HorizontalAlignment hAlign, VerticalAlignment vAlign) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(bold);
        font.setItalic(italic);
        font.setFontHeight(height);
        style.setFont(font);
        style.setAlignment(hAlign);
        style.setVerticalAlignment(vAlign);
        style.setWrapText(wrap);
        return style;
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Чек-лист " + checklist.getObject().toString());
        Row titleRow = sheet.createRow(rowCount);
        sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 2));
        createCell(titleRow, 0,
                "Чек лист оценки состояния объекта - " + checklist.getObject().toString(),
                cellStyle(workbook, true, false, false, 16,
                        HorizontalAlignment.CENTER, VerticalAlignment.CENTER));
        rowCount++;
        Row row2 = sheet.createRow(++rowCount);
        sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 2));
        createCell(row2, 0, "Обследование проводил: " + checklist.getMaster().getFullName(),
                cellStyle(workbook, false, true, false, 14,
                        HorizontalAlignment.LEFT, VerticalAlignment.CENTER));
        Row row3 = sheet.createRow(++rowCount);
        sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 2));
        createCell(row3, 0, "Должность: " + checklist.getMaster().getPosition().getName(),
                cellStyle(workbook, false, true, false, 14,
                        HorizontalAlignment.LEFT, VerticalAlignment.CENTER));
        rowCount++;
        Row row4 = sheet.createRow(++rowCount);
        createCell(row4, 0, "№ п/п",
                cellStyle(workbook, true, false, false, 14,
                        HorizontalAlignment.CENTER, VerticalAlignment.CENTER));
        createCell(row4, 1, "Наименование критерия (характеристики)",
                cellStyle(workbook, true, false, false, 14,
                        HorizontalAlignment.CENTER, VerticalAlignment.CENTER));
        createCell(row4, 2, "Оценка",
                cellStyle(workbook, true, false, false, 14,
                        HorizontalAlignment.CENTER, VerticalAlignment.CENTER));
    }

    private void writeFooterLine() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        rowCount++;
        Row row = sheet.createRow(++rowCount);
        createCell(row, 1, "Общая оценка:",
                cellStyle(workbook, true, true, false, 14,
                        HorizontalAlignment.RIGHT, VerticalAlignment.CENTER));
        createCell(row, 2, checklist.getOverallGrade(),
                cellStyle(workbook, true, false, false, 14,
                        HorizontalAlignment.CENTER, VerticalAlignment.CENTER));
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        if (columnCount == 1) {
            sheet.setColumnWidth(columnCount, 25000);
        } else {
            sheet.autoSizeColumn(columnCount);
        }
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue(String.valueOf(value));
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        List<Criterion> criteria = criterionRepository.findAll();
        for (Criterion criterion : criteria) {
            Row criterionRow = sheet.createRow(++rowCount);
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount++, 0, 2));
            createCell(criterionRow, 0, criterion.getName(), criterionStyle());
            for (Measure measure : criterion.getMeasures()) {
                Row measureRow = sheet.createRow(rowCount++);

                boolean checkedMeasure = checkMeasureRepository
                        .findCheckMeasureByChecklistAndMeasure(checklist, measure)
                        .orElse(null) != null;
                int columnCount = 0;
                createCell(measureRow, columnCount++, measure.getId(),
                        cellStyle(workbook, false, false, false, 14,
                                HorizontalAlignment.CENTER, VerticalAlignment.CENTER));
                createCell(measureRow, columnCount++, measure.getName(),
                        cellStyle(workbook, false, false, true, 14,
                                HorizontalAlignment.LEFT, VerticalAlignment.CENTER));
                createCell(measureRow, columnCount,
                        checkedMeasure ? String.valueOf(measure.getGrade()) : String.valueOf(0.00),
                        gradeStyle(checkedMeasure));
            }
        }
    }

    private CellStyle criterionStyle() {
        CellStyle criterionStyle = cellStyle(workbook, false, false, false, 14,
                HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        criterionStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        criterionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return criterionStyle;
    }

    private CellStyle gradeStyle(boolean checkedMeasure) {
        CellStyle gradeStyle = cellStyle(workbook, false, false, false, 14,
                HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        if (checkedMeasure) {
            gradeStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        } else {
            gradeStyle.setFillForegroundColor(IndexedColors.CORAL.getIndex());
        }
        gradeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return gradeStyle;
    }

    public void export(HttpServletResponse response, Checklist checklist) throws IOException {
        this.checklist = checklist;
        workbook = new XSSFWorkbook();
        rowCount = 0;
        writeHeaderLine();
        writeDataLines();
        writeFooterLine();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    //TODO тест отправки
//    public void exportToMail(??? Stream out, Checklist checklist) throws IOException {
//        this.checklist = checklist;
//        workbook = new XSSFWorkbook();
//        rowCount = 0;
//        writeHeaderLine();
//        writeDataLines();
//        writeFooterLine();
//        workbook.write(out);
//        workbook.close();
//        out.close();
//    }
}
