package com.aldenleon.treeter.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class StepRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int priority;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment stepParent;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment stepChild;
}
