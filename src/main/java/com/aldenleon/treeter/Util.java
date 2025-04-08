package com.aldenleon.treeter;

import com.aldenleon.treeter.model.Comment;
import com.aldenleon.treeter.projection.CommentProjection;
import org.owasp.html.HtmlChangeListener;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public final class Util {
    private Util() {} // not instantiable

    private static Comment projectionToComment(CommentProjection c) {
        return new Comment(c.getId(), c.getTextContent(), c.getUp(), c.getDw(),
                null, null, null, null);
    }

    private static void nest(Comment root, int rootIndex, List<CommentProjection> projectionList) {
        List<Comment> children = new ArrayList<>();
        int i = rootIndex + 1;
        while (i < projectionList.size()) {
            //&& !Objects.equals(projectionList.get(i).getParentId(), projectionList.get(rootIndex).getParentId())) {
            // only makes sense if children in projectionList are always under their parent.
            // that's not the case if two sibling comments have the same score and (at least) one of them has children.
            CommentProjection projection = projectionList.get(i);
            if (projection.getParentId() == root.getId()) {
                Comment child = projectionToComment(projection);
                //child.setParent(root); // avoid infinite recursion
                nest(child, i, projectionList);
                children.add(child);
            }
            i++;
        }
        if (! children.isEmpty()) {
            root.setChildren(children);
        }
    }

    public static Comment projectionListToNestedComments(List<CommentProjection> projectionList) {
        if (projectionList == null || projectionList.isEmpty()) return null;
        Comment root = projectionToComment(projectionList.getFirst());
        if (projectionList.size() == 1 ||
                projectionList.get(1).getParentId() != root.getId() ) { // only one root element
            return root;
        }
        List<Comment> children = new ArrayList<>();
        for (int i = 1; i < projectionList.size(); i++) {
            CommentProjection projection = projectionList.get(i);
            if (projection.getParentId() == root.getId()) {
                Comment child = projectionToComment(projection);
                //child.setParent(root); // avoid infinite recursion
                nest(child, i, projectionList);
                children.add(child);
            }
        }
        if (! children.isEmpty()) {
            root.setChildren(children);
        }
        return root;
    }

    public static String sanitizeHTML(String unsafeHTML, Logger log) {
        PolicyFactory sanitizer = Sanitizers.FORMATTING
                .and(Sanitizers.BLOCKS)
                .and(Sanitizers.LINKS)
                .and(Sanitizers.TABLES);
        List<String> sanitizerChanges = new ArrayList<>();
        String safeHTML = sanitizer.sanitize(unsafeHTML,
                new HtmlChangeListener<List<String>>() {
                    @Override
                    public void discardedTag(List<String> strings, String s) {
                        strings.add(s);
                    }

                    @Override
                    public void discardedAttributes(List<String> strings, String s, String... strings2) {
                        strings.add(s + "(" + String.join(" ", strings2) + ")");
                    }
                }, sanitizerChanges);
        if (!sanitizerChanges.isEmpty()) {
            log.warn("""
                            unsafe comment:
                            ---------------
                            {}
                            ---------------
                            dangerous parts: {}""", unsafeHTML, sanitizerChanges);
            return null;
        }
        return safeHTML;
    }

}
