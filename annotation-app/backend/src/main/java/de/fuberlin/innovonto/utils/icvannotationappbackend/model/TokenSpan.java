package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TokenSpan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int start;
    private int end;

    //hibernate
    public TokenSpan() {
    }

    public long getId() {
        return id;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
