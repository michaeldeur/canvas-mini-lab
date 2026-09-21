package com.example.canvasminilab.controller;

import com.example.canvasminilab.dto.Assignment;
import com.example.canvasminilab.dto.Course;
import com.example.canvasminilab.dto.UserProfile;
import com.example.canvasminilab.service.CanvasApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CanvasController {

    private final CanvasApiService apiService;

    public CanvasController(CanvasApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/")
    public String index(Model model) {
        try {
            UserProfile profile = apiService.getUserProfile();
            List<Course> courses = apiService.getCourses();

            model.addAttribute("profile", profile);
            model.addAttribute("courses", courses);
            return "index";
        } catch (Exception e) {
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

            model.addAttribute("profile", profile);
            model.addAttribute("courses", courses);
            model.addAttribute("assignments", assignments);
            model.addAttribute("selectedCourseId", courseId);
            return "index";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }
}