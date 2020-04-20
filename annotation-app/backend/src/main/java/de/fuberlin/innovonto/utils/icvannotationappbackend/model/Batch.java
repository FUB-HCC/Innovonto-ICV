package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
public class Batch {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "de.fuberlin.innovonto.utils.common.FallbackUUIDGenerator"
    )
    private UUID id;
    private LocalDateTime created;
    private LocalDateTime lastPublished;
    private LocalDateTime submitted;

    //TODO save as string instead of magic number
    private BatchState batchState = BatchState.UNALLOCATED;

    private String hitId;
    private String workerId;
    private String assignmentId;
    private UUID resultsAnnotationSessionId;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Idea> ideas;

    //hibernate
    public Batch() {
        this.created = LocalDateTime.now();
    }


}
