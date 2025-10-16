package it.controller;

import it.service.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tree")
public class TreeController {

    @Autowired
    private TreeService treeService;

    @PostMapping("/generate")
    public Map<String, Object> generateTree(@RequestParam Long videoId) {
        System.out.println("开始生成知识树图谱");
        return treeService.generateTree(videoId);
    }
}

