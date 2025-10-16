// 文件：src/main/java/it/service/WordExportService.java
package it.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface WordExportService {
    void exportNotesAsWord(Long videoId, HttpServletResponse response) throws IOException;
    void exportTreeAsWord(Long videoId, HttpServletResponse response) throws IOException;
}
