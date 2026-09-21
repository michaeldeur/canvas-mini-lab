/**
 * Author: Michael Deur
 * Date: Fri Sep 18 07:49:22 AM MDT 2026
 * Description: This class is the controller for managing the 3 API endpoints using in
 * this project
 */
package com.example;

import com.example.canvasminilab.controller.CanvasController;
import com.example.canvasminilab.dto.Assignment;
import com.example.canvasminilab.dto.Course;
import com.example.canvasminilab.dto.UserProfile;
import com.example.canvasminilab.service.CanvasApiService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CanvasController.class)
class CanvasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CanvasApiService apiService;

    @Test
    @DisplayName("Test 8: GET / loads dashboard with profile and course model attributes")
    void testIndexEndpoint() throws Exception {
        UserProfile profile = new UserProfile();
        profile.setName("Test Student");

        Course course = new Course();
        course.setId(1001L);
        course.setName("CS 408");

        given(apiService.getUserProfile()).willReturn(profile);
        given(apiService.getCourses()).willReturn(List.of(course));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attributeExists("courses"));
    }

    @Test
    @DisplayName("Test 9: POST /assignments populates assignments for selected course")
    void testGetAssignmentsEndpoint() throws Exception {
        UserProfile profile = new UserProfile();
        Course course = new Course();
        course.setId(1001L);

        Assignment assignment = new Assignment();
        assignment.setId(501L);
        assignment.setName("Mini-Lab 1");

        given(apiService.getUserProfile()).willReturn(profile);
        given(apiService.getCourses()).willReturn(List.of(course));
        given(apiService.getAssignments(1001L)).willReturn(List.of(assignment));

        mockMvc.perform(post("/assignments").param("courseId", "1001"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("assignments"))
                .andExpect(model().attribute("selectedCourseId", 1001L));
    }

    @Test
    @DisplayName("Test 10: Graceful error handling when Canvas API throws an exception")
    void testErrorHandlingWhenApiFails() throws Exception {
        given(apiService.getUserProfile()).willThrow(new RuntimeException("401 Unauthorized"));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));
    }
}