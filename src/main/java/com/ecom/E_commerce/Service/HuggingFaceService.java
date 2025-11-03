package com.ecom.E_commerce.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

@Service
public class HuggingFaceService {

    @Value("${huggingface.api.url}")
    private String apiUrl;

    @Value("${huggingface.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();


    public String analyzeSentiment(String inputText) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("inputs", inputText);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            return "{\"error\": \"HuggingFace API error: " + ex.getStatusCode() + "\"}";
        } catch (Exception e) {
            return "{\"error\": \"General error: " + e.getMessage() + "\"}";
        }
    }

    private String extractTopSentimentLabel(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode predictions = root.get(0); // First list in the outer array

            String topLabel = null;
            double topScore = -1;

            for (JsonNode prediction : predictions) {
                double score = prediction.get("score").asDouble();
                if (score > topScore) {
                    topScore = score;
                    topLabel = prediction.get("label").asText();
                }
            }

            return "Sentiment: " + mapLabelToText(topLabel) + " (Confidence: " + String.format("%.2f", topScore * 100) + "%)";
        } catch (Exception e) {
            return "Failed to parse sentiment result: " + e.getMessage();
        }
    }

    private String mapLabelToText(String label) {
        return switch (label) {
            case "LABEL_0" -> "Negative";
            case "LABEL_1" -> "Neutral";
            case "LABEL_2" -> "Positive";
            case "POSITIVE", "NEGATIVE" -> label.charAt(0) + label.substring(1).toLowerCase();
            default -> label;
        };
    }
}
