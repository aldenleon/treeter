package com.aldenleon.treeter.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 1024)
    private String textContent;

    private int up;
    private int dw;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Comment> children;

    @OneToMany(mappedBy = "stepParent", fetch = FetchType.LAZY)
    private List<StepRelation> stepChildren;

    @OneToMany(mappedBy = "stepChild", fetch = FetchType.LAZY)
    private List<StepRelation> stepParents;
}
