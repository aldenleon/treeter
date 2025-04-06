package com.aldenleon.treeter.controller;

import com.aldenleon.treeter.Util;
import com.aldenleon.treeter.model.Comment;
import com.aldenleon.treeter.projection.CommentProjection;
import com.aldenleon.treeter.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final CommentRepository commentRepository;

    @RequestMapping("/list-tree-view")
    public String listTree(@RequestParam(name = "root-id", defaultValue="1") Long rootId,
                             @RequestParam(name = "max-depth", defaultValue="100") int maxDepth,
                             @RequestParam(name = "page-size", defaultValue="100") int pageSize,
                             // Thymeleaf front needs only one root element
                             //@RequestParam(name = "page", defaultValue="0") int page,
                             Model model) {
        List<CommentProjection> projectionList = commentRepository.findTreeById(rootId, maxDepth, pageSize, 0); //page);
        Comment rootComment = Util.projectionListToNestedComments(projectionList);
        model.addAttribute("rootComment", rootComment);
        return "list-tree";
    }
}
