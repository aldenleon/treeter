package com.aldenleon.treeter.projection;

public interface StepRelationProjection {
    Long getId();
    int getPriority();
    Long getStepParentId();
    Long getStepChildId();
}
