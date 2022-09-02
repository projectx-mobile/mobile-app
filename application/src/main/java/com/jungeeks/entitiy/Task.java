package com.jungeeks.entitiy;

import com.jungeeks.entitiy.enums.TASK_STATUS;
import com.jungeeks.entitiy.enums.TASK_TYPE;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Long points;
    private Date deadline;
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status")
    private TASK_STATUS taskStatus;
    private boolean daily;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type")
    private TASK_TYPE taskType;

    @Column(name = "family_id")
    private String familyId;


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_photo", joinColumns = @JoinColumn(name = "task_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "path", column = @Column(name = "path"))
    })
    private List<Photo> photo;

}