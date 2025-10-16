package it.controller;

import it.service.WordExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/export")
public class WordExportController {

    @Autowired
    private WordExportService wordExportService;

    @PostMapping("/notes")
    public void exportNotes(@RequestParam Long videoId,
                            HttpServletResponse response) throws IOException {
        wordExportService.exportNotesAsWord(videoId, response);
    }

    @PostMapping("/tree")
    public void exportTree(@RequestParam Long videoId,
                           HttpServletResponse response) throws IOException {
        wordExportService.exportTreeAsWord(videoId, response);
    }
}
