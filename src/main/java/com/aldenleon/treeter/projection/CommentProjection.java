package com.aldenleon.treeter.projection;

public interface CommentProjection {
    Long getId();
    String getTextContent();
    int getUp();
    int getDw();
    Long getParentId();
}
