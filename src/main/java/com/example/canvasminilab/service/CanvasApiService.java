package com.example.canvasminilab.service;

import com.example.canvasminilab.dto.Course;
import com.example.canvasminilab.dto.Assignment;
import com.example.canvasminilab.dto.UserProfile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CanvasApiService {

    @Value("${canvas.api.token}")
    private String apiToken;

    @Value("${canvas.base.url}")
    private String baseUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Pattern NEXT_LINK_PATTERN = Pattern.compile("<([^>]+)>;\\s*rel=\"next\"");

    // Endpoint 1: User Profile
    public UserProfile getUserProfile() throws IOException, InterruptedException {
        String url = baseUrl + "/api/v1/users/self/profile";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiToken)
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Canvas API Error (HTTP " + response.statusCode() + "): Check your token.");
        }
        return objectMapper.readValue(response.body(), UserProfile.class);
    }

    // Endpoint 2: Active Courses with Enrollments & Total Scores (for grade parsing)
    public List<Course> getCourses() throws IOException, InterruptedException {
        String url = baseUrl + "/api/v1/courses?enrollment_state=active&include[]=enrollments&include[]=total_scores";
        return fetchPaginatedData(url, new TypeReference<List<Course>>() {});
    }

    // Endpoint 3: Assignments for Selected Course
    public List<Assignment> getAssignments(Long courseId) throws IOException, InterruptedException {
        String url = baseUrl + "/api/v1/courses/" + courseId + "/assignments";
        return fetchPaginatedData(url, new TypeReference<List<Assignment>>() {});
    }

    private <T> List<T> fetchPaginatedData(String initialUrl, TypeReference<List<T>> typeReference)
            throws IOException, InterruptedException {

        List<T> combinedResults = new ArrayList<>();
        String currentUrl = initialUrl;

        while (currentUrl != null) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(currentUrl))
                    .header("Authorization", "Bearer " + apiToken)
                    .GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 401) {
                throw new RuntimeException("401 Unauthorized: Invalid or missing Canvas API Token in .env");
            } else if (response.statusCode() != 200) {
                throw new RuntimeException("Canvas API Error (HTTP " + response.statusCode() + "): " + response.body());
            }

            List<T> pageItems = objectMapper.readValue(response.body(), typeReference);
            combinedResults.addAll(pageItems);

            Optional<String> linkHeader = response.headers().firstValue("Link");
            currentUrl = linkHeader.map(this::extractNextUrl).orElse(null);
        }

        return combinedResults;
    }

    private String extractNextUrl(String linkHeader) {
        Matcher matcher = NEXT_LINK_PATTERN.matcher(linkHeader);
        return matcher.find() ? matcher.group(1) : null;
    }
}