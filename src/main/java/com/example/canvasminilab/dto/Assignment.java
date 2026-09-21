package com.example.canvasminilab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Assignment {
    private Long id;
    private String name;

    @JsonProperty("due_at")
    private String dueAt;

    @JsonProperty("points_possible")
    private Double pointsPossible;

    @JsonProperty("html_url")
    private String htmlUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDueAt() { return dueAt; }
    public void setDueAt(String dueAt) { this.dueAt = dueAt; }
    public Double getPointsPossible() { return pointsPossible; }
    public void setPointsPossible(Double pointsPossible) { this.pointsPossible = pointsPossible; }
    public String getHtmlUrl() { return htmlUrl; }
    public void setHtmlUrl(String htmlUrl) { this.htmlUrl = htmlUrl; }
}