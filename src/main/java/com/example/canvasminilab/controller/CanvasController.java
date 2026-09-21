package com.example.canvasminilab.controller;

import com.example.canvasminilab.dto.Assignment;
import com.example.canvasminilab.dto.Course;
import com.example.canvasminilab.dto.UserProfile;
import com.example.canvasminilab.service.CanvasApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CanvasController {

    private static final Logger log = LoggerFactory.getLogger(CanvasController.class);
    private final CanvasApiService apiService;

    public CanvasController(CanvasApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/")
    public String index(Model model) {
        try {
            UserProfile profile = apiService.getUserProfile();
            List<Course> courses = apiService.getCourses();

            log.info("==================================================");
            log.info("CANVAS DASHBOARD LOADED");
            log.info("--- USER PROFILE DETAILS ---");
            log.info("User ID:     {}", profile.getId());
            log.info("Name:        {}", profile.getName());
            log.info("----------------------------");
            log.info("Active Enrolled Courses: {}", courses.size());
            courses.forEach(c -> log.info(" -> [{}] {} | Score: {}",
                    c.getId(),
                    c.getName(),
                    c.getCurrentScore() != null ? c.getCurrentScore() + "%" : "N/A"));
            log.info("==================================================");

            model.addAttribute("profile", profile);
            model.addAttribute("courses", courses);
            return "index";
        } catch (Exception e) {
            log.error("Failed to load Canvas dashboard: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/assignments")
    public String getAssignments(@RequestParam("courseId") Long courseId, Model model) {
        try {
            UserProfile profile = apiService.getUserProfile();
            List<Course> courses = apiService.getCourses();
            List<Assignment> assignments = apiService.getAssignments(courseId);

            log.info("==================================================");
            log.info("FETCHED ASSIGNMENTS FOR COURSE ID: {}", courseId);
            log.info("--- USER PROFILE DETAILS ---");
            log.info("User ID:     {}", profile.getId());
            log.info("Name:        {}", profile.getName());
            log.info("Avatar URL:  {}", profile.getAvatarUrl());
            log.info("----------------------------");
            log.info("Total Assignments Found: {}", assignments.size());
            assignments.forEach(a -> log.info(" -> Assignment: {} | Points: {} | Due: {}",
                    a.getName(),
                    a.getPointsPossible() != null ? a.getPointsPossible() : "N/A",
                    a.getDueAt() != null ? a.getDueAt() : "No Due Date"));
            log.info("==================================================");

            model.addAttribute("profile", profile);
            model.addAttribute("courses", courses);
            model.addAttribute("assignments", assignments);
            model.addAttribute("selectedCourseId", courseId);
            return "index";
        } catch (Exception e) {
            log.error("Failed to fetch assignments for course {}: {}", courseId, e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }
}