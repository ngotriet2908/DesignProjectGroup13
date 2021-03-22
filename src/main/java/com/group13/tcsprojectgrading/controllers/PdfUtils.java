package com.group13.tcsprojectgrading.controllers;

import com.group13.tcsprojectgrading.models.Assessment;
import com.group13.tcsprojectgrading.models.grading.CriterionGrade;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.rubric.Element;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.rubric.RubricContent;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.group13.tcsprojectgrading.controllers.ProjectsController.addEmptyLine;

public class PdfUtils {
    private Document document;
    private Rubric rubric;
    private Assessment assessment;
    private Map<String, NodeInfo> nodeInfoMap;
    private int maxLevel;

    public PdfUtils(Document document, Rubric rubric, Assessment assessment) {
        this.document = document;
        this.rubric = rubric;
        this.assessment = assessment;
        this.maxLevel = 0;
        nodeInfoMap = new HashMap<>();
    }

    public void headerGenerator(String title, String body) {

    }


    public Document generatePdfOfFeedback() throws IOException {
        if (rubric.getChildren() == null) return null;
        nodeInfoMap.put("-1", new NodeInfo(0, null, 0, "-1","P", 0, 0));
        NodeInfo nodeInfo = nodeInfoMap.get("-1");

        for(Element child: rubric.getChildren()) {
            visitNode(child, child);
        }

        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        Paragraph header = new Paragraph("Final sum grade: " + nodeInfo.getSumGrade())
                .setFont(font)
                .setFontSize(20);
        addEmptyLine(header, 3);

        document.add(header);

        for(Element child: rubric.getChildren()) {
            visitWriteNode(child);
        }
        return document;
    }

    public void visitWriteNode(Element currentNode) throws IOException {
        RubricContent content = currentNode.getContent();
        NodeInfo nodeInfo = nodeInfoMap.get(currentNode.getContent().getId());
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        float fixPadding = 10* (nodeInfo.getLevel() - 1);
        if (content.getType().equals(RubricContent.CRITERION_TYPE)) {
            Paragraph header = new Paragraph(nodeInfo.label + " " + content.getTitle() + ": " + nodeInfo.getGrade().getGrade())
                    .setFont(font)
                    .setPaddingLeft(fixPadding)
                    .setFontSize(13);
            document.add(header);
            //TODO check for null comment
            Paragraph body = new Paragraph("comment: " + nodeInfo.getGrade().getComment())
                    .setFont(font)
                    .setPaddingLeft(fixPadding + 10)
                    .setFontSize(12);
            addEmptyLine(body, 1);
            document.add(body);

        } else {
            Paragraph blockBreak = new Paragraph(" ")
                    .setFont(font)
                    .setFontSize(20);
//            addEmptyLine(blockBreak, 1);
            document.add(blockBreak);

            Paragraph header = new Paragraph(nodeInfo.label + " " + content.getTitle() + ": " + nodeInfo.getSumGrade())
                    .setFont(font)
                    .setPaddingLeft(fixPadding)
                    .setFontSize(15 + 2*(maxLevel - nodeInfo.getLevel()));

            addEmptyLine(header, 1);
            document.add(header);

            if (currentNode.getChildren() != null) {
                for(Element element1: currentNode.getChildren()) {
                    visitWriteNode(element1);
                }
            }
        }
    }

    public void visitNode(Element currentNode, Element parentNode) {
        RubricContent content = currentNode.getContent();
        if (content.getType().equals(RubricContent.CRITERION_TYPE)) {
            CriterionGrade grade = assessment.getGrades().get(content.getId());
            if (grade.getHistory().size() > grade.getActive()) {
                NodeInfo nodeInfo = null;

                //level 0
                if (currentNode.getContent().getId().equals(parentNode.getContent().getId())) {
                    nodeInfoMap.get("-1").setCriterionCount(nodeInfoMap.get("-1").getCriterionCount() + 1);
                    maxLevel = Math.max(maxLevel , 1);
                    nodeInfo = new NodeInfo(
                            1,
                            grade.getHistory().get(grade.getActive()),
                            -1,
                            "-1",
                            "C" + nodeInfoMap.get("-1").getCriterionCount(),
                            0,
                            0
                            );
                } else if (nodeInfoMap.containsKey(parentNode.getContent().getId())) {
                    maxLevel = Math.max(maxLevel , nodeInfoMap.get(parentNode.getContent().getId()).getLevel() + 1);

                    NodeInfo parentInfo = nodeInfoMap.get(parentNode.getContent().getId());
                    parentInfo.setCriterionCount(parentInfo.getCriterionCount() + 1);
                    nodeInfo = new NodeInfo(
                            nodeInfoMap.get(parentNode.getContent().getId()).getLevel() + 1,
                            grade.getHistory().get(grade.getActive()),
                            -1,
                            parentNode.getContent().getId(),
                            "C" + parentInfo.getLabel().substring(1) + "." + parentInfo.getCriterionCount(),
                            0,
                            0
                    );
                }

                if (nodeInfo == null) return;

                nodeInfoMap.put(currentNode.getContent().getId(), nodeInfo);

                if (nodeInfoMap.containsKey(parentNode.getContent().getId()) &&
                        !currentNode.getContent().getId().equals(parentNode.getContent().getId())) {
                    nodeInfoMap.get(parentNode.getContent().getId()).setSumGrade(
                            nodeInfoMap.get(parentNode.getContent().getId()).getSumGrade() +
                                    grade.getHistory().get(grade.getActive()).getGrade()
                    );
                } else if (currentNode.getContent().getId().equals(parentNode.getContent().getId())) {
                    nodeInfoMap.get("-1").setSumGrade(nodeInfoMap.get("-1").getSumGrade() + nodeInfo.getGrade().getGrade());
                }
                return;
            }
        }

        //Block type

        NodeInfo nodeInfo = null;

        //level 0
        if (currentNode.getContent().getId().equals(parentNode.getContent().getId())) {
            nodeInfoMap.get("-1").setBlockCount(nodeInfoMap.get("-1").getBlockCount() + 1);
            maxLevel = Math.max(maxLevel , 1);

            nodeInfo = new NodeInfo(
                    1,
                    null,
                    0,
                    "-1",
                    "B" + nodeInfoMap.get("-1").getBlockCount(),
                    0,
                    0
            );
        } else if (nodeInfoMap.containsKey(parentNode.getContent().getId())) {
            NodeInfo parentInfo = nodeInfoMap.get(parentNode.getContent().getId());
            parentInfo.setBlockCount(parentInfo.getCriterionCount() + 1);
            maxLevel = Math.max(maxLevel , nodeInfoMap.get(parentNode.getContent().getId()).getLevel() + 1);
            nodeInfo = new NodeInfo(
                    nodeInfoMap.get(parentNode.getContent().getId()).getLevel() + 1,
                    null,
                    0,
                    parentNode.getContent().getId(),
                    "B" + parentInfo.getLabel().substring(1) + "." + parentInfo.getBlockCount(),
                    0,
                    0
            );
        }

        if (nodeInfo == null) return;

        nodeInfoMap.put(currentNode.getContent().getId(), nodeInfo);

        if (currentNode.getChildren() != null) {
            for(Element element1: currentNode.getChildren()) {
                visitNode(element1, currentNode);
                if (nodeInfoMap.containsKey(parentNode.getContent().getId()) &&
                        !currentNode.getContent().getId().equals(parentNode.getContent().getId())) {
                    nodeInfoMap.get(parentNode.getContent().getId()).setSumGrade(
                            nodeInfoMap.get(parentNode.getContent().getId()).getSumGrade() +
                                    nodeInfoMap.get(currentNode.getContent().getId()).getSumGrade()
                    );
                }
            }
            if (currentNode.getContent().getId().equals(parentNode.getContent().getId())) {
                nodeInfoMap.get("-1").setSumGrade(nodeInfoMap.get("-1").getSumGrade() + nodeInfo.getSumGrade());
            }
        }
    }

    public class NodeInfo {
        private int level;
        private Grade grade;
        private int sumGrade;
        private String parentId;
        private String label;
        private int blockCount;
        private int criterionCount;

        public NodeInfo(int level, Grade grade, int sumGrade, String parentId, String label, int blockCount, int criterionCount) {
            this.level = level;
            this.grade = grade;
            this.sumGrade = sumGrade;
            this.parentId = parentId;
            this.label = label;
            this.blockCount = blockCount;
            this.criterionCount = criterionCount;
        }

        public int getSumGrade() {
            return sumGrade;
        }

        public void setSumGrade(int sumGrade) {
            this.sumGrade = sumGrade;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public Grade getGrade() {
            return grade;
        }

        public void setGrade(Grade grade) {
            this.grade = grade;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getBlockCount() {
            return blockCount;
        }

        public void setBlockCount(int blockCount) {
            this.blockCount = blockCount;
        }

        public int getCriterionCount() {
            return criterionCount;
        }

        public void setCriterionCount(int criterionCount) {
            this.criterionCount = criterionCount;
        }
    }

}
