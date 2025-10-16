package it.controller;

import it.pojo.VideoNote;
import it.service.VideoNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/notes")
public class VideoNoteController {

    @Autowired
    private VideoNoteService videoNoteService;
    @PostMapping("/generate")
    public ResponseEntity<Map<String,Object>> generate(@RequestParam Long videoId) {
        System.out.println("接收到生成笔记请求，videoId = " + videoId);

        List<VideoNote> notes = videoNoteService.generateNoteByVideoId(videoId);
        System.out.println("VideoNote 数量 = " + (notes != null ? notes.size() : 0));


        Map<Integer, List<VideoNote>> grouped = new TreeMap<>();
        for (VideoNote vn : notes) {
            grouped.computeIfAbsent(vn.getNoteIndex(), k -> new ArrayList<>()).add(vn);
        }

        List<Map<String, Object>> directory = new ArrayList<>();
        List<Map<String, Object>> content = new ArrayList<>();

        for (Map.Entry<Integer, List<VideoNote>> entry : grouped.entrySet()) {
            int noteIndex = entry.getKey();
            List<VideoNote> list = entry.getValue();
            String chapterTitle = list.get(0).getChapterTitle();
            directory.add(Map.of(
                    "noteIndex", noteIndex,
                    "label", noteIndex + ". " + chapterTitle
            ));

            for (VideoNote vn : list) {
                Map<String, Object> contentEntry = new LinkedHashMap<>();
                contentEntry.put("noteIndex", noteIndex);
                contentEntry.put("content", vn.getContent());

                if (vn.getSubIndex() != null) {
                    directory.add(Map.of(
                            "noteIndex", noteIndex,
                            "subIndex", vn.getSubIndex(),
                            "label", noteIndex + "-" + vn.getSubIndex() + ". " + vn.getTitle()
                    ));

                    contentEntry.put("subIndex", vn.getSubIndex());
                    contentEntry.put("title", noteIndex + "-" + vn.getSubIndex() + ". " + vn.getTitle());
                } else {
                    contentEntry.put("title", noteIndex + ". " + vn.getChapterTitle());
                }

                content.add(contentEntry);
            }
        }

        Map<String,Object> resp = Map.of(
                "code", 200,
                "directory", directory,
                "notes", content
        );

        System.out.println("返回内容构造完成，目录数 = " + directory.size() + "，笔记数 = " + content.size());
        return ResponseEntity.ok(resp);
    }

}


