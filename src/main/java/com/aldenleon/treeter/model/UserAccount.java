package com.aldenleon.treeter.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
// "user" is a reserved keyword in postgresql
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    private String externalId;
    private String externalIdProvider;
    private String name;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY)
    private List<Vote> votes;
}
