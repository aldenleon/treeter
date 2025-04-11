package com.aldenleon.treeter.repository;

import com.aldenleon.treeter.model.StepRelation;
import com.aldenleon.treeter.projection.StepRelationProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.CrudRepository;

public interface StepRelationRepository extends CrudRepository<StepRelation, Long> {
    @Transactional
    @NativeQuery("""
            insert into step_relation (priority, step_child_id, step_parent_id)
            values (:priority, :stepChildId, :stepParentId)
            returning id, priority, step_child_id, step_parent_id;
            """)
    StepRelationProjection save(int priority, Long stepChildId, Long stepParentId);
}
