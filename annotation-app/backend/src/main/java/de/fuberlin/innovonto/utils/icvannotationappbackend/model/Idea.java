package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Idea {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "de.fuberlin.innovonto.utils.common.FallbackUUIDGenerator"
    )
    private UUID id;

    @Column(length = 10_000)
    private String content;
    @Column(length = 2_000)
    private String hasIdeaContest;

    //Hibernate
    public Idea() {
    }

    public Idea(UUID id, String content, String hasIdeaContest) {
        this.id = id;
        this.content = content;
        this.hasIdeaContest = hasIdeaContest;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    //TODO is this the right approach? This always assumes a uuid in the URL. should i use full urls instead?
    @JsonSetter("@id")
    public void setIdFromJson(String id) {
        String localUUID = id.split("/")[id.split("/").length - 1];
        this.id = UUID.fromString(localUUID);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHasIdeaContest() {
        return hasIdeaContest;
    }

    public void setHasIdeaContest(String hasIdeaContest) {
        this.hasIdeaContest = hasIdeaContest;
    }

    @Override
    public String toString() {
        return "Idea{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", hasIdeaContest='" + hasIdeaContest + '\'' +
                '}';
    }
}
