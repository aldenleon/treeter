package com.aldenleon.treeter;

import com.aldenleon.treeter.model.Comment;
import com.aldenleon.treeter.projection.CommentProjection;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Util {

    private static Comment projectionToComment(CommentProjection c) {
        return new Comment(c.getId(), c.getTextContent(), c.getUp(), c.getDw(),
                null, null, null, null);
    }

    private static void nest(Comment root, int rootIndex, List<CommentProjection> projectionList) {
        List<Comment> children = new LinkedList<>();
        int i = rootIndex + 1;
        while (i < projectionList.size() &&
                !Objects.equals(projectionList.get(i).getParentId(), projectionList.get(rootIndex).getParentId())) {
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
        List<Comment> children = new LinkedList<>();
        for (int i = 1; i < projectionList.size(); i++) {
            CommentProjection projection = projectionList.get(i);
            if (projection.getParentId() == root.getId()) {
                Comment child = projectionToComment(projection);
                //child.setParent(root); // avoid infinite recursion
                nest(child, i, projectionList);
                children.add(child);
            }
        }
        root.setChildren(children);
        return root;
    }

}
