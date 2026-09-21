/**
 * Author: Michael Deur
 * Date: Fri Sep 18 07:49:22 AM MDT 2026
 * Description: This class is a data transfer object that maps the raw JSON provided
 * by the course endpoint to a Java object so it can be used by the program. Enrollment
 * and grades are also handled here as well.
 */
package com.example.canvasminilab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Course {
    private Long id;
    private String name;

    @JsonProperty("course_code")
    private String courseCode;

    private List<Enrollment> enrollments;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }

    /** This helper method used to find the current score from an enrollment object
     *
     * @return double containing currentScore
     */
    public Double getCurrentScore() {
        if (enrollments != null && !enrollments.isEmpty()) {
            for (Enrollment e : enrollments) {
                if (e.getComputedCurrentScore() != null) {
                    return e.getComputedCurrentScore();
                }
                if (e.getCurrentScore() != null) {
                    return e.getCurrentScore();
                }
                if (e.getGrades() != null) {
                    if (e.getGrades().getComputedCurrentScore() != null) {
                        return e.getGrades().getComputedCurrentScore();
                    }
                    if (e.getGrades().getCurrentScore() != null) {
                        return e.getGrades().getCurrentScore();
                    }
                }
            }
        }
        return null;
    }

    public boolean isGraded() {
        return getCurrentScore() != null;
    }

    /**This class is a data transfer object that maps the raw JSON provided
     * by the enrollment endpoint to a Java object so it can be used by the program.      *
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Enrollment {
        @JsonProperty("computed_current_score")
        private Double computedCurrentScore;

        @JsonProperty("current_score")
        private Double currentScore;

        private Grades grades;

        public Double getComputedCurrentScore() { return computedCurrentScore; }
        public void setComputedCurrentScore(Double computedCurrentScore) { this.computedCurrentScore = computedCurrentScore; }

        public Double getCurrentScore() { return currentScore; }
        public void setCurrentScore(Double currentScore) { this.currentScore = currentScore; }

        public Grades getGrades() { return grades; }
        public void setGrades(Grades grades) { this.grades = grades; }
    }

    /**This class is a data transfer object that maps the raw JSON provided
     * by the grades endpoint to a Java object so it can be used by the program.      *
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Grades {
        @JsonProperty("computed_current_score")
        private Double computedCurrentScore;

        @JsonProperty("current_score")
        private Double currentScore;

        public Double getComputedCurrentScore() { return computedCurrentScore; }
        public void setComputedCurrentScore(Double computedCurrentScore) { this.computedCurrentScore = computedCurrentScore; }

        public Double getCurrentScore() { return currentScore; }
        public void setCurrentScore(Double currentScore) { this.currentScore = currentScore; }
    }
}