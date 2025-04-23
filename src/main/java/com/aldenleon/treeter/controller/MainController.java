package com.aldenleon.treeter.controller;

import com.aldenleon.treeter.Util;
import com.aldenleon.treeter.dto.NewCommentDto;
import com.aldenleon.treeter.exception.CommentNotFoundException;
import com.aldenleon.treeter.model.Comment;
import com.aldenleon.treeter.projection.CommentProjection;
import com.aldenleon.treeter.projection.StepRelationProjection;
import com.aldenleon.treeter.repository.CommentRepository;
import com.aldenleon.treeter.repository.StepRelationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final CommentRepository commentRepository;
    private final StepRelationRepository stepRelationRepository;

    @RequestMapping("/list-tree-view")
    public String listTree(@RequestParam(name = "root-id", defaultValue="1") Long rootId,
                             @RequestParam(name = "max-depth", defaultValue="100") int maxDepth,
                             @RequestParam(name = "page-size", defaultValue="100") int pageSize,
                             @AuthenticationPrincipal OAuth2User principal,
                             Model model) {
        List<CommentProjection> projectionList = commentRepository.findTreeById(rootId, maxDepth, pageSize, 0);
        Comment rootComment = Util.projectionListToNestedComments(projectionList);

        if (rootComment == null) {
            log.warn("invalid id requested");
            throw new CommentNotFoundException();
        }

        String user = null;
        if (principal != null) {
            user = principal.getName();
        }

        model.addAttribute("user", user);
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

    private String dropComment(NewCommentDto newComment, Model model) {
        model.addAttribute("newComment", newComment);
        model.addAttribute("errors", true);
        return "reply";
    }

    @PostMapping("/reply")
    public String reply(@Valid @ModelAttribute NewCommentDto newComment, BindingResult bindingResult,
                        Model model) {
        if (bindingResult.hasErrors()) {
            log.warn("invalid comment dropped");
            return dropComment(newComment, model);
        }

        newComment.setTextContent(Util.sanitizeHTML(newComment.getTextContent(), log));
        if (newComment.getTextContent() == null) {
            log.warn("unsafe comment dropped");
            return dropComment(newComment, model);
        }

        List<Long> stepParentIds = new ArrayList<>();
        if (newComment.getStepParents() != null) {
            for (String splitted : newComment.getStepParents().trim().split(";", 0)) {
                String item = splitted.trim();
                if (! item.isEmpty()) {
                    long id;
                    try {
                        id = Long.parseLong(item);
                    } catch (NumberFormatException e) {
                        log.warn("comment with stepParent has invalid number formatting");
                        return dropComment(newComment, model);
                    }
                    if (id == newComment.getParentId()) {
                        log.warn("tried to save parent as step-parent");
                        return dropComment(newComment, model);
                    }
                    if (! commentRepository.existsById(id)) {
                        log.warn("comment with step-parent has invalid step-parent ID");
                        return dropComment(newComment, model);
                    }
                    stepParentIds.add(id);
                }
            }
        }

        log.info("new comment received at /reply, saving to database");
        CommentProjection savedComment = commentRepository.save(newComment.getParentId(),
                newComment.getTextContent(), 1, 0);
        log.info("new comment saved with id {}", savedComment.getId());

        if (newComment.getStepParents() != null && ! stepParentIds.isEmpty()) {
            for (int i = 0; i < stepParentIds.size(); i++) {
                log.info("new step-relation received at /reply, saving to database");
                StepRelationProjection savedStepRelation = stepRelationRepository.save(i, savedComment.getId(),
                        stepParentIds.get(i));
                log.info("new step-relation saved with id {}", savedStepRelation.getId());
            }
        }

        return "redirect:/list-tree-view?root-id=" + newComment.getRootId() +
                "&max-depth=" + newComment.getMaxDepth() + "&page-size=" + newComment.getPageSize();
    }
}
