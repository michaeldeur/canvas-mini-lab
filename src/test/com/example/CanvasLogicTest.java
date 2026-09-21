package com.example;

import com.example.canvasminilab.dto.Course;
import com.example.canvasminilab.dto.UserProfile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CanvasLogicTest {

    @Test
    @DisplayName("Test 1: Course getCurrentScore returns correct score from enrollment")
    void testCourseGetCurrentScoreSuccess() {
        Course course = new Course();
        Course.Enrollment enrollment = new Course.Enrollment();
        enrollment.setComputedCurrentScore(92.5);
        course.setEnrollments(List.of(enrollment));

        assertEquals(92.5, course.getCurrentScore());
        assertTrue(course.isGraded());
    }

    @Test
    @DisplayName("Test 2: Course handles fallback nested Grades object correctly")
    void testCourseGetNestedGradesScore() {
        Course course = new Course();
        Course.Enrollment enrollment = new Course.Enrollment();
        Course.Grades grades = new Course.Grades();
        grades.setCurrentScore(88.0);
        enrollment.setGrades(grades);
        course.setEnrollments(List.of(enrollment));

        assertEquals(88.0, course.getCurrentScore());
        assertTrue(course.isGraded());
    }

    @Test
    @DisplayName("Test 3: Course with no enrollments or scores returns null for getCurrentScore (N/A)")
    void testCourseUngradedReturnsNull() {
        Course course = new Course();
        course.setEnrollments(Collections.emptyList());

        assertNull(course.getCurrentScore());
        assertFalse(course.isGraded());
    }

    @Test
    @DisplayName("Test 4: GPA calculation correctly converts percentages to 4.0 scale")
    void testGpaCalculationStandard() {
        Course c1 = createCourse(95.0); // 4.0
        Course c2 = createCourse(88.0); // 3.3
        Course c3 = createCourse(75.0); // 2.0

        List<Course> courses = List.of(c1, c2, c3);
        double totalPoints = courses.stream()
                .mapToDouble(c -> percentageToGpa(c.getCurrentScore()))
                .sum();

        double calculatedGpa = Math.round((totalPoints / courses.size()) * 100.0) / 100.0;
        assertEquals(3.1, calculatedGpa);
    }

    @Test
    @DisplayName("Test 5: Ungraded courses (null score) are excluded from GPA calculation")
    void testGpaCalculationFiltersUngradedCourses() {
        Course graded1 = createCourse(90.0); // 3.7
        Course graded2 = createCourse(84.0); // 3.0
        Course ungraded = createCourse(null); // N/A - Ignored

        List<Course> activeCourses = List.of(graded1, graded2, ungraded);
        List<Course> validCourses = activeCourses.stream().filter(Course::isGraded).toList();

        assertEquals(2, validCourses.size());

        double totalPoints = validCourses.stream()
                .mapToDouble(c -> percentageToGpa(c.getCurrentScore()))
                .sum();

        double gpa = Math.round((totalPoints / validCourses.size()) * 100.0) / 100.0;
        assertEquals(3.35, gpa);
    }

    @Test
    @DisplayName("Test 6: Percentage boundary conditions (93%+ -> 4.0, <60% -> 0.0)")
    void testPercentageToGpaBoundaries() {
        assertEquals(4.0, percentageToGpa(93.0));
        assertEquals(3.7, percentageToGpa(90.0));
        assertEquals(3.0, percentageToGpa(83.0));
        assertEquals(1.0, percentageToGpa(60.0));
        assertEquals(0.0, percentageToGpa(59.9));
    }

    @Test
    @DisplayName("Test 7: UserProfile DTO correctly sets and retrieves fields")
    void testUserProfileDto() {
        UserProfile profile = new UserProfile();
        profile.setId(101L);
        profile.setName("Jane Doe");
        profile.setAvatarUrl("https://canvas.example.com/avatar.png");

        assertEquals(101L, profile.getId());
        assertEquals("Jane Doe", profile.getName());
        assertEquals("https://canvas.example.com/avatar.png", profile.getAvatarUrl());
    }

    private double percentageToGpa(double percentage) {
        if (percentage >= 93.0) return 4.0;
        if (percentage >= 90.0) return 3.7;
        if (percentage >= 87.0) return 3.3;
        if (percentage >= 83.0) return 3.0;
        if (percentage >= 80.0) return 2.7;
        if (percentage >= 77.0) return 2.3;
        if (percentage >= 73.0) return 2.0;
        if (percentage >= 70.0) return 1.7;
        if (percentage >= 67.0) return 1.3;
        if (percentage >= 60.0) return 1.0;
        return 0.0;
    }

    private Course createCourse(Double score) {
        Course course = new Course();
        if (score != null) {
            Course.Enrollment enrollment = new Course.Enrollment();
            enrollment.setComputedCurrentScore(score);
            course.setEnrollments(List.of(enrollment));
        } else {
            course.setEnrollments(Collections.emptyList());
        }
        return course;
    }
}