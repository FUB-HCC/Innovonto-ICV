package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class AnnotationProject {
    @Id
    private String id;

    private LocalDateTime created;

    //TODO include metadata.

    @OneToMany(cascade = CascadeType.ALL)
    private List<Batch> batches;

    @OneToMany(cascade = CascadeType.ALL)
    private List<MturkAnnotationSession> sessions;


}
