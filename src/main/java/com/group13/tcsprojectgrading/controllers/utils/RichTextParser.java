package com.group13.tcsprojectgrading.controllers.utils;

import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RichTextParser {

    private final PdfFont normalFont;
    private final PdfFont codeFont;
    private static final int normalFontSize = 12;

    public RichTextParser() throws IOException {

        PdfFontFactory.register("src/main/resources/font/FontsFree-Net-SFMono-Regular.ttf","CodeFont");
        PdfFontFactory.register("src/main/resources/font/Roboto-Regular.ttf","Roboto");
        codeFont = PdfFontFactory.createRegisteredFont("CodeFont");
        normalFont = PdfFontFactory.createRegisteredFont("Roboto");
    }

    public static void main(String[] args) throws IOException {
        String html = "<p>When<u> the server </u>is started, it will ask the <strong>user to input a port number</strong> <em>where it will listen to</em>. If this number is already in use, <u><em><strong>the server</strong></em></u> will <em><strong>ask again</strong></em>.</p>\n<p>When <strong>the server </strong>is started, it will ask the <code>user to input a port number</code> where it will listen to. If this number is <em>already in </em><u><em>use, </em></u><u><em><strong>the </strong></em></u><u><strong>server will</strong></u><u> ask</u> again.</p>";
        org.jsoup.nodes.Document document = Jsoup.parse(html);

        Element root = document.body();
        RichTextParser richTextParser = new RichTextParser();

        String FILE_NAME = "src/main/resources/richTextParser.pdf";
        File targetFile = new File(FILE_NAME);
        targetFile.delete();
        Path newFilePath = Paths.get(FILE_NAME);
        Files.createFile(newFilePath);

        OutputStream out = new FileOutputStream(FILE_NAME);
        PdfWriter pdfWriter = new PdfWriter(out);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document documentPdf = new Document(pdfDocument, PageSize.A4);
        documentPdf.getPdfDocument();
        Paragraph paragraph = richTextParser.visit(root, 0, new Paragraph());
        documentPdf.add(paragraph);
        documentPdf.close();
    }

    public Paragraph parse(String html) throws IOException {
        org.jsoup.nodes.Document document = Jsoup.parse(html);
        Element root = document.body();
        return visit(root, 0, new Paragraph());
    }

    public Paragraph visit(Node node, int level, Paragraph paragraph) throws IOException {
        for(Node node1: node.childNodes()) {
            //is paragraph
            if (node1 instanceof Element && node1.nodeName().equals("p")) {
                Paragraph paragraph1 = new Paragraph();
                List<Text> textList = visitParagraph(node1, level + 1, new ArrayList<Text>());
                for(Text text: textList) {
                    paragraph1.add(text);
                }
                paragraph.add(paragraph1);
            } else {
                continue;
            }
        }
//        return visit(node, level, new ArrayList<Text>());
        return paragraph;
    }

    private List<Text> visitParagraph(Node node, int level, List<Text> textList) throws IOException {

        if (node instanceof Element) {
//            Element element = (Element) node;
            for(Node node1: node.childNodes()) {
                if (node1 instanceof TextNode) {
                    TextNode textNode = (TextNode) node1;
                    Text text = new Text(textNode.text())
                            .setFont(this.normalFont)
                            .setFontSize(normalFontSize);
                    textList.add(text);
                    continue;
                }

                List<Text> textList1 = new ArrayList<>();
                visitParagraph(node1, level + 1, textList1);

                switch (node1.nodeName()) {
                    case "strong":
                        for(Text text: textList1) text.setBold();
                        break;
                    case "u":
                        for(Text text: textList1) text.setUnderline(0.6f, -2f);
                        break;
                    case "em":
                        for(Text text: textList1) text.setItalic();
                        break;
                    case "code":
//                        Style style = new Style();
//                        style.setFont(FontConstants.TIMES);
//                        style.setFontColor(new DeviceRgb(232, 62, 140));
//                        style.setFontSize((float) (normalFontSize*0.5));
//                        style.setWordSpacing(4);
                        for(Text text: textList1) {
                            text.setFont(codeFont);
                            text.setFontColor(new DeviceRgb(232, 62, 140));
                            text.setFontSize((float) (normalFontSize*0.875));
                            text.setWordSpacing(-1F);
                        }
                        break;
                }
                if (textList1.size() > 0) {
                    textList.addAll(textList1);
//                    paragraph.setKeepTogether(true);
//                    paragraph.setKeepWithNext(true);
                }
            }
            return textList;
        }
        return null;
    }
}
