package com.aldenleon.treeter.controller;

import com.aldenleon.treeter.Util;
import com.aldenleon.treeter.model.Comment;
import com.aldenleon.treeter.projection.CommentProjection;
import com.aldenleon.treeter.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class ApiController {
    private final CommentRepository commentRepository;

    @GetMapping("/list")
    List<CommentProjection> listCommentProjections(@RequestParam(name = "root-id", defaultValue="1") Long rootId,
                                                   @RequestParam(name = "max-depth", defaultValue="100") int maxDepth,
                                                   @RequestParam(name = "page-size", defaultValue="100") int pageSize,
                                                   @RequestParam(name = "page", defaultValue="0") int page) {
        return commentRepository.findTreeById(rootId, maxDepth, pageSize, page);
    }

    @GetMapping("/list-nested")
    Comment listNestedComments(@RequestParam(name = "root-id", defaultValue="1") Long rootId,
                               @RequestParam(name = "max-depth", defaultValue="100") int maxDepth,
                               @RequestParam(name = "page-size", defaultValue="100") int pageSize,
                               @RequestParam(name = "page", defaultValue="0") int page) {
        List<CommentProjection> projectionList = commentRepository.findTreeById(rootId, maxDepth, pageSize, page);
        return Util.projectionListToNestedComments(projectionList);
    }
}
