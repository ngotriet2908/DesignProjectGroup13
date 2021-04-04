package com.group13.tcsprojectgrading.controllers;

import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.grading.AssessmentLink;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.rubric.Element;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.user.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;

public class ExcelUtils {
    private Map<User, AssessmentLink> assessmentMap;
    private Rubric rubric;
    private Workbook workbook;

    public ExcelUtils(Map<User, AssessmentLink> assessmentMap, Rubric rubric, Workbook workbook) {
        this.assessmentMap = assessmentMap;
        this.rubric = rubric;
        this.workbook = workbook;
    }

    public ExcelUtils(Map<User, AssessmentLink> assessmentMap, Rubric rubric) {
        this.assessmentMap = assessmentMap;
        this.rubric = rubric;
        workbook = new XSSFWorkbook();
    }

    public void addParticipantsGradePage() {
        Sheet sheet = workbook.createSheet("Participants");
        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setWrapText(true);
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        String[] headerNames = {"name", "sid", "submission id", "submission name", "assessment id"};
        int i = 0;
        for(; i < headerNames.length; i++) {
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue(headerNames[i]);
            headerCell.setCellStyle(headerStyle);
        }

        List<Element> criteria = rubric.fetchAllCriteria();
        Collections.reverse(criteria);
        Map<Integer, String> columnsMapping = new LinkedHashMap<>();
        for(Element criterion: criteria) {
            Cell headerCell = header.createCell(i);
            columnsMapping.put(i, criterion.getContent().getId());
            i += 1;
            headerCell.setCellValue(criterion.getContent().getTitle());
            headerCell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for(Map.Entry<User, AssessmentLink> entry: assessmentMap.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(entry.getKey().getName());

            cell = row.createCell(1);
            cell.setCellValue(entry.getKey().getsNumber());

            if (entry.getValue() == null) continue;

            cell = row.createCell(2);
            cell.setCellValue(entry.getValue().getId().getSubmission().getId());

            cell = row.createCell(3);
            cell.setCellValue(entry.getValue().getId().getSubmission().getName());

            cell = row.createCell(4);
            cell.setCellValue(entry.getValue().getId().getAssessment().getId());

            for(Map.Entry<Integer, String> entry1: columnsMapping.entrySet()) {
                cell = row.createCell(entry1.getKey());
                Double grade = getGradeFromString(entry1.getValue(), entry.getValue().getId().getAssessment().getGrades());
                if (grade == null) {
                    cell.setCellValue("");
                } else {
                    cell.setCellValue(grade);
                }
            }
        }

    }

    private Double getGradeFromString(String criterionId, Set<Grade> gradeSet) {
        for(Grade grade: gradeSet) {
            if (grade.getCriterionId().equals(criterionId)) {
                return (double) grade.getGrade();
            }
        }
        return null;
    }

    public Workbook getWorkbook() {
        return workbook;
    }
}
