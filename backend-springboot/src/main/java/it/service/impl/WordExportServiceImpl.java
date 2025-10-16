package it.service.impl;

import it.pojo.VideoNote;
import it.service.TreeService;
import it.service.VideoNoteService;
import it.service.WordExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class WordExportServiceImpl implements WordExportService {

    @Autowired
    private VideoNoteService videoNoteService;

    @Autowired
    private TreeService treeService;

    @Override
    public void exportNotesAsWord(Long videoId, HttpServletResponse response) throws IOException {
        List<VideoNote> notes = videoNoteService.generateNoteByVideoId(videoId);
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=VideoNotes_" + videoId + ".docx");

        try (XWPFDocument doc = new XWPFDocument()) {
            // 标题
            XWPFParagraph title = doc.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = title.createRun();
            run.setText("视频 " + videoId + " 结构化笔记");
            run.setBold(true);
            run.setFontSize(18);

            // 遍历笔记
            for (VideoNote vn : notes) {
                if (vn.getSubIndex() == null) {
                    // 一级标题
                    XWPFParagraph chap = doc.createParagraph();
                    chap.setSpacingBefore(200);
                    XWPFRun chapRun = chap.createRun();
                    chapRun.setText(vn.getNoteIndex() + ". " + vn.getChapterTitle());
                    chapRun.setBold(true);
                    chapRun.setFontSize(14);
                } else {
                    // 二级标题
                    XWPFParagraph sec = doc.createParagraph();
                    sec.setIndentationLeft(400);
                    XWPFRun secRun = sec.createRun();
                    secRun.setText(vn.getNoteIndex() + "-" + vn.getSubIndex() + ". " + vn.getTitle());
                    secRun.setItalic(true);
                    secRun.setFontSize(12);
                }
                // 内容段落
                XWPFParagraph p = doc.createParagraph();
                p.setIndentationLeft(vn.getSubIndex() == null ? 0 : 600);
                XWPFRun contentRun = p.createRun();
                contentRun.setText(vn.getContent());
                contentRun.setFontSize(12);
            }

            doc.write(response.getOutputStream());
        }
    }

    @Override
    public void exportTreeAsWord(Long videoId, HttpServletResponse response) throws IOException {
        Map<String, Object> treeResult = treeService.generateTree(videoId);
        Object nodeData = treeResult.get("nodeData");

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=KnowledgeTree_" + videoId + ".docx");

        try (XWPFDocument doc = new XWPFDocument()) {
            // 标题
            XWPFParagraph title = doc.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = title.createRun();
            run.setText("视频 " + videoId + " 知识树");
            run.setBold(true);
            run.setFontSize(18);

            // 递归输出树节点
            appendNode(doc, nodeData, 0);

            doc.write(response.getOutputStream());
        }
    }

    @SuppressWarnings("unchecked")
    private void appendNode(XWPFDocument doc, Object nodeObj, int level) {
        if (!(nodeObj instanceof Map)) return;
        Map<String, Object> node = (Map<String, Object>) nodeObj;
        String topic = (String) node.get("topic");

        XWPFParagraph p = doc.createParagraph();
        p.setIndentationLeft(level * 400);
        XWPFRun run = p.createRun();
        run.setText(topic);
        run.setBold(level == 0);
        run.setFontSize(12 + Math.max(0, 3 - level));

        List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
        if (children != null) {
            for (Map<String, Object> child : children) {
                appendNode(doc, child, level + 1);
            }
        }
    }
}