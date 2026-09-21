/**
 * Author: Michael Deur
 * Date: Fri Sep 18 07:49:22 AM MDT 2026
 * Description: This class is a data transfer object that maps the raw JSON provided
 * by the user profile endpoint to Java object so it can be used by the program.
 */
package com.example.canvasminilab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {
    private Long id;
    private String name;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}