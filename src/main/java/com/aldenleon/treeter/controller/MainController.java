package com.aldenleon.treeter.controller;

import com.aldenleon.treeter.Util;
import com.aldenleon.treeter.dto.NewCommentDto;
import com.aldenleon.treeter.model.Comment;
import com.aldenleon.treeter.projection.CommentProjection;
import com.aldenleon.treeter.repository.CommentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final CommentRepository commentRepository;

    @RequestMapping("/list-tree-view")
    public String listTree(@RequestParam(name = "root-id", defaultValue="1") Long rootId,
                             @RequestParam(name = "max-depth", defaultValue="100") int maxDepth,
                             @RequestParam(name = "page-size", defaultValue="100") int pageSize,
                             Model model) {
        List<CommentProjection> projectionList = commentRepository.findTreeById(rootId, maxDepth, pageSize, 0);
        Comment rootComment = Util.projectionListToNestedComments(projectionList);
        model.addAttribute("rootComment", rootComment);
        model.addAttribute("rootId", rootId);
        model.addAttribute("maxDepth", maxDepth);
        model.addAttribute("pageSize", pageSize);
        return "list-tree";
    }

    @GetMapping("/reply")
    public String reply(@RequestParam(name = "parent-id", defaultValue = "1") Long parentId,
                        @RequestParam(name = "root-id", defaultValue="1") Long rootId,
                        @RequestParam(name = "max-depth", defaultValue="100") int maxDepth,
                        @RequestParam(name = "page-size", defaultValue="100") int pageSize,
                        Model model) {
        NewCommentDto newComment = new NewCommentDto();
        newComment.setParentId(parentId);
        newComment.setRootId(rootId);
        newComment.setMaxDepth(maxDepth);
        newComment.setPageSize(pageSize);
        model.addAttribute("newComment", newComment);
        return "reply";
    }

    private void dropComment(NewCommentDto newComment, Model model) {
        model.addAttribute("newComment", newComment);
        model.addAttribute("errors", true);
    }

    @PostMapping("/reply")
    public String reply(@Valid @ModelAttribute NewCommentDto newComment, BindingResult bindingResult,
                        Model model) {
        if (bindingResult.hasErrors()) {
            log.warn("invalid comment dropped");
            dropComment(newComment, model);
            return "reply";
        }

        newComment.setTextContent(Util.sanitizeHTML(newComment.getTextContent(), log));
        if (newComment.getTextContent() == null) {
            dropComment(newComment, model);
            return "reply";
        }

        log.info("new comment received at /reply, saving to database");
        CommentProjection saved = commentRepository.save(newComment.getParentId(), newComment.getTextContent(), 1, 0);
        log.info("new comment saved with id {}", saved.getId());

        return "redirect:/list-tree-view?root-id=" + newComment.getRootId() +
                "&max-depth=" + newComment.getMaxDepth() + "&page-size=" + newComment.getPageSize();
    }
}
