package com.group13.tcsprojectgrading.controllers;

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
import com.itextpdf.layout.property.HorizontalAlignment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.group13.tcsprojectgrading.controllers.ProjectsController.addEmptyLine;

public class PdfRubricUtils {
    private final Document document;
    private final Rubric rubric;
    private final Map<String, NodeInfo> nodeInfoMap;
    private int maxLevel;

    public PdfRubricUtils(Document document, Rubric rubric) {
        this.document = document;
        this.rubric = rubric;
        this.maxLevel = 0;
        nodeInfoMap = new HashMap<>();
    }

    public void generateRubrics() throws IOException {
        if (rubric.getChildren() == null) return;
        nodeInfoMap.put("-1", new NodeInfo(0,"-1","P", 0, 0));
        NodeInfo nodeInfo = nodeInfoMap.get("-1");

        for(Element child: rubric.getChildren()) {
            visitNode(child, child);
        }

        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        Paragraph header = new Paragraph("Rubric")
                .setFont(font)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setFontSize(25);
        addEmptyLine(header, 3);

        document.add(header);

        for(Element child: rubric.getChildren()) {
            visitWriteNode(child);
        }
        document.close();
    }

    public void visitWriteNode(Element currentNode) throws IOException {
        RubricContent content = currentNode.getContent();
        NodeInfo nodeInfo = nodeInfoMap.get(currentNode.getContent().getId());
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        float fixPadding = 10* (nodeInfo.getLevel() - 1);
        if (content.getType().equals(RubricContent.CRITERION_TYPE)) {
            Paragraph header = new Paragraph(nodeInfo.label + " " + content.getTitle())
                    .setFont(font)
                    .setPaddingLeft(fixPadding)
                    .setFontSize(13);
            document.add(header);
            //TODO check for null comment
            RichTextParser richTextParser = new RichTextParser();
            Paragraph body = richTextParser.parse(content.getText())
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

            Paragraph header = new Paragraph(nodeInfo.label + " " + content.getTitle())
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
                NodeInfo nodeInfo = null;
                //level 0
                if (currentNode.getContent().getId().equals(parentNode.getContent().getId())) {
                    nodeInfoMap.get("-1").setCriterionCount(nodeInfoMap.get("-1").getCriterionCount() + 1);
                    maxLevel = Math.max(maxLevel , 1);
                    nodeInfo = new NodeInfo(
                            1,
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
                            parentNode.getContent().getId(),
                            "C" + parentInfo.getLabel().substring(1) + "." + parentInfo.getCriterionCount(),
                            0,
                            0
                    );
                }

                if (nodeInfo == null) return;

                nodeInfoMap.put(currentNode.getContent().getId(), nodeInfo);
                return;
        }

        //Block type

        NodeInfo nodeInfo = null;

        //level 0
        if (currentNode.getContent().getId().equals(parentNode.getContent().getId())) {
            nodeInfoMap.get("-1").setBlockCount(nodeInfoMap.get("-1").getBlockCount() + 1);
            maxLevel = Math.max(maxLevel , 1);

            nodeInfo = new NodeInfo(
                    1,
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
            }
        }
    }
    
    public class NodeInfo {
        private int level;
        private String parentId;
        private String label;
        private int blockCount;
        private int criterionCount;

        public NodeInfo(int level, String parentId, String label, int blockCount, int criterionCount) {
            this.level = level;

            this.parentId = parentId;
            this.label = label;
            this.blockCount = blockCount;
            this.criterionCount = criterionCount;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
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
