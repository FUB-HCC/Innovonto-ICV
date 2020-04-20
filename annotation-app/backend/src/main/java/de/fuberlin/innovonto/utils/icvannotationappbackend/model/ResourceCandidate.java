package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import org.json.JSONObject;

import javax.persistence.*;

/*
{
  "confidence": 0.0,
  "description": "Villa Elaine was Remy Zero's second studio album, released in 1998 and produced for Geffen Records. After recording the album, Radiohead, who the band had toured with previously, added Remy Zero to their OK Computer tour. The band ended up touring with Scottish band, Travis. The album became popular for the song \"Prophecy\", which was used on the soundtrack for the movies She's All That and The Last Kiss. The song \"Fair\" was used on Zach Braff's Grammy award-winning soundtrack to the movie Garden State and, more recently, featured in the movie Fanboys. \"Hermes Bird\" was used in the TV series Felicity and Charmed. \"Problem\" appeared on the soundtrack for the Drew Barrymore film Never Been Kissed. There was even a song, named `Villa Elain`.",
  "label": "Villa Elaine",
  "offset": 0,
  "resource": "http://dbpedia.org/resource/Villa_Elaine",
  "source": "babelfy",
  "text": "problem",
  "thumbnail": null,
  "token_span": {
    "end": 0,
    "start": 0
  }
}
 */
@Entity
public class ResourceCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String text;
    @Column(name = "token_offset")
    private long offset;

    @Column(length = 5_000)
    private String resource;

    @Column
    private String source;
    @Column
    private double confidence;
    @Column
    private boolean selected;

    public ResourceCandidate() {
    }

    public ResourceCandidate(JSONObject input) {
        //TODO
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
