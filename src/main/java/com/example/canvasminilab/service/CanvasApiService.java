/**
 * Author: Michael Deur
 * Date: Fri Sep 18 07:49:22 AM MDT 2026
 * Description: Service layer responsible for communicating with the Canvas REST API.
 * This class does token authentication, executes HTTP requests, deserializes JSON responses
 * into DTOs, and navigates paginated API responses using the HTTP Link headers.
 */
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

    /** This is for an endpoint and is used to retrieve the data from the user profile endpoint.
     *
     * @return a user profile of type UserProfile
     * @throws IOException
     * @throws InterruptedException
     */
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

    /** This is for an endpoint and is used to retrieve the data from the course endpoint.
     *
     * @return a list of courses of type Course
     * @throws IOException
     * @throws InterruptedException
     */
    public List<Course> getCourses() throws IOException, InterruptedException {
        String url = baseUrl + "/api/v1/courses?enrollment_state=active&include[]=enrollments&include[]=total_scores";
        return fetchPaginatedData(url, new TypeReference<List<Course>>() {});
    }

    /** This is for an endpoint and is used to retrieve the data from the assignment endpoint.
     *
     * @return a list of assignments of type Assignment
     * @throws IOException
     * @throws InterruptedException
     */
    public List<Assignment> getAssignments(Long courseId) throws IOException, InterruptedException {
        String url = baseUrl + "/api/v1/courses/" + courseId + "/assignments";
        return fetchPaginatedData(url, new TypeReference<List<Assignment>>() {});
    }

    /** This helper method executes HTTP GET requests in a loop to fetch all pages of
     * data for any data type (Course, Assignment, etc.) and combines them into a list.
     *
     * @param <T> The data type of list objects
     * @param initialUrl The starting API endpoint URL for the initial request
     * @param typeReference The Jackson TypeReference
     * @return A list containing all aggregated records across all pages
     * @throws IOException
     * @throws InterruptedException
     */
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

    /**
     * Parses an HTTP Link header string using regex to locate and extract
     * the URL corresponding to the rel="next" relation.
     *
     * @param linkHeader The raw header string
     * @return The absolute URL for the next page of results, or null if no next page.
     */
    private String extractNextUrl(String linkHeader) {
        Matcher matcher = NEXT_LINK_PATTERN.matcher(linkHeader);
        return matcher.find() ? matcher.group(1) : null;
    }
}