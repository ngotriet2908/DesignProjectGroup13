package com.group13.tcsprojectgrading.controllers;

import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.rubric.Element;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.rubric.RubricContent;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.HorizontalAlignment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CanvasFeedbackUtils {
    private String feedback;
    private final Rubric rubric;
    private final Assessment assessment;
    private final Map<String, CanvasFeedbackUtils.NodeInfo> nodeInfoMap;
    private final CourseParticipation participation;
    private int maxLevel;

    public CanvasFeedbackUtils(String feedback, Rubric rubric, Assessment assessment, CourseParticipation participation) {
        this.feedback = feedback;
        this.rubric = rubric;
        this.assessment = assessment;
        this.participation = participation;
        this.maxLevel = 0;
        nodeInfoMap = new HashMap<>();
    }

    public void headerGenerator(String title, String body) {

    }

    private Grade findActiveGradeForCriterion(String criterion) {
        for(Grade grade: this.assessment.getGrades()) {
            if (grade.getActive() && grade.getCriterionId().equals(criterion)) {
                return grade;
            }
        }
        return null;
    }

    public String generateFeedbackString() throws IOException {
        if (rubric.getChildren() == null) return null;
        nodeInfoMap.put("-1", new CanvasFeedbackUtils.NodeInfo(0, null, 0, "-1","P", 0, 0));
        CanvasFeedbackUtils.NodeInfo nodeInfo = nodeInfoMap.get("-1");

        for(Element child: rubric.getChildren()) {
            visitNode(child, child);
        }

        feedback += "\n\n";
        feedback += "Feedback for " + participation.getId().getUser().getName();
        feedback += "\n";

        feedback += "Final grade: " + ((assessment.getManualGrade() != null)? assessment.getManualGrade() : assessment.getFinalGrade());
        feedback += "\n\n";

        for(Element child: rubric.getChildren()) {
            visitWriteNode(child);
        }
        return feedback;
    }

    public void visitWriteNode(Element currentNode) throws IOException {
        RubricContent content = currentNode.getContent();
        CanvasFeedbackUtils.NodeInfo nodeInfo = nodeInfoMap.get(currentNode.getContent().getId());
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        int fixPadding = 2* (nodeInfo.getLevel() - 1);
        if (content.getType().equals(RubricContent.CRITERION_TYPE)) {

            feedback += " ".repeat(fixPadding) + nodeInfo.label + " " + content.getTitle() + ": " + nodeInfo.getGrade().getGrade();
            feedback += "\n";

            if (nodeInfo.getGrade().getDescription() != null &&
                    !nodeInfo.getGrade().getDescription().equals("null") &&
                    !nodeInfo.getGrade().getDescription().equals("none")
            ) {
                feedback += " ".repeat(fixPadding + 2) + "comment: " + nodeInfo.getGrade().getDescription();
                feedback += "\n\n";
            }

        } else {
            feedback += "\n";

            feedback += " ".repeat(fixPadding) + nodeInfo.label + " " + content.getTitle() + ": ";
            feedback += "\n";

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
            Grade grade = findActiveGradeForCriterion(content.getId());
            if (grade != null) {
                CanvasFeedbackUtils.NodeInfo nodeInfo = null;

                //level 0
                if (currentNode.getContent().getId().equals(parentNode.getContent().getId())) {
                    nodeInfoMap.get("-1").setCriterionCount(nodeInfoMap.get("-1").getCriterionCount() + 1);
                    maxLevel = Math.max(maxLevel , 1);
                    nodeInfo = new CanvasFeedbackUtils.NodeInfo(
                            1,
                            grade,
                            -1,
                            "-1",
                            "C" + nodeInfoMap.get("-1").getCriterionCount(),
                            0,
                            0
                    );
                } else if (nodeInfoMap.containsKey(parentNode.getContent().getId())) {
                    maxLevel = Math.max(maxLevel , nodeInfoMap.get(parentNode.getContent().getId()).getLevel() + 1);

                    CanvasFeedbackUtils.NodeInfo parentInfo = nodeInfoMap.get(parentNode.getContent().getId());
                    parentInfo.setCriterionCount(parentInfo.getCriterionCount() + 1);
                    nodeInfo = new CanvasFeedbackUtils.NodeInfo(
                            nodeInfoMap.get(parentNode.getContent().getId()).getLevel() + 1,
                            grade
                            ,
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
                                    grade.getGrade()
                    );
                } else if (currentNode.getContent().getId().equals(parentNode.getContent().getId())) {
                    nodeInfoMap.get("-1").setSumGrade(nodeInfoMap.get("-1").getSumGrade() + nodeInfo.getGrade().getGrade());
                }
                return;
            }
        }

        //Block type

        CanvasFeedbackUtils.NodeInfo nodeInfo = null;

        //level 0
        if (currentNode.getContent().getId().equals(parentNode.getContent().getId())) {
            nodeInfoMap.get("-1").setBlockCount(nodeInfoMap.get("-1").getBlockCount() + 1);
            maxLevel = Math.max(maxLevel , 1);

            nodeInfo = new CanvasFeedbackUtils.NodeInfo(
                    1,
                    null,
                    0,
                    "-1",
                    "B" + nodeInfoMap.get("-1").getBlockCount(),
                    0,
                    0
            );
        } else if (nodeInfoMap.containsKey(parentNode.getContent().getId())) {
            CanvasFeedbackUtils.NodeInfo parentInfo = nodeInfoMap.get(parentNode.getContent().getId());
            parentInfo.setBlockCount(parentInfo.getCriterionCount() + 1);
            maxLevel = Math.max(maxLevel , nodeInfoMap.get(parentNode.getContent().getId()).getLevel() + 1);
            nodeInfo = new CanvasFeedbackUtils.NodeInfo(
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
        private float sumGrade;
        private String parentId;
        private String label;
        private int blockCount;
        private int criterionCount;

        public NodeInfo(int level, Grade grade, float sumGrade, String parentId, String label, int blockCount, int criterionCount) {
            this.level = level;
            this.grade = grade;
            this.sumGrade = sumGrade;
            this.parentId = parentId;
            this.label = label;
            this.blockCount = blockCount;
            this.criterionCount = criterionCount;
        }

        public float getSumGrade() {
            return sumGrade;
        }

        public void setSumGrade(float sumGrade) {
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

    public static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

}
