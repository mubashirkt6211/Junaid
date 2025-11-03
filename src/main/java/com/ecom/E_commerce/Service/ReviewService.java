package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Config.JwtService;
import com.ecom.E_commerce.Model.Item;
import com.ecom.E_commerce.Model.Review;
import com.ecom.E_commerce.Model.User;
import com.ecom.E_commerce.Repository.ItemRepository;
import com.ecom.E_commerce.Repository.ReviewRepository;
import com.ecom.E_commerce.Repository.UserRepository;
import com.ecom.E_commerce.Request.ReviewRequest;
import com.ecom.E_commerce.Response.ReviewResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ItemRepository itemRepository;
    private final ReviewRepository reviewRepository;
    private final HuggingFaceService huggingFaceService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public Review submitReview(ReviewRequest request, String token) {

        String userEmail = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        String sentimentRaw = huggingFaceService.analyzeSentiment(request.getContent());
        String sentiment = extractTopSentiment(sentimentRaw);

        Review review = new Review();
        review.setContent(request.getContent());
        review.setSentiment(sentiment);
        review.setItem(item);
        review.setUser(user);
        review.setUsername(user.getFirstName());

        return reviewRepository.save(review);
    }


    private String extractTopSentiment(String responseJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<List<Map<String, Object>>> outer = mapper.readValue(responseJson, List.class);

            if (outer.isEmpty()) {
                System.out.println("Empty response array.");
                return "UNKNOWN";
            }

            List<Map<String, Object>> inner = outer.get(0);

            Map<String, Object> top = inner.stream()
                    .max(Comparator.comparing(m -> (Double) m.get("score")))
                    .orElseThrow();

            String label = ((String) top.get("label")).toUpperCase();
            System.out.println("Top label: " + label + ", Score: " + top.get("score"));

            return switch (label) {
                case "LABEL_0", "NEGATIVE" -> "NEGATIVE";
                case "LABEL_1", "NEUTRAL" -> "NEUTRAL";
                case "LABEL_2", "POSITIVE" -> "POSITIVE";
                default -> "UNKNOWN";
            };
        } catch (Exception e) {
            e.printStackTrace();
            return "UNKNOWN";
        }
    }


    public List<ReviewResponseDTO> getReviewsByItem(Long itemId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        List<Review> reviews = reviewRepository.findByItem(item);

        return reviews.stream()
                .map(r -> new ReviewResponseDTO(
                        r.getId(),
                        r.getContent(),
                        r.getSentiment(),
                        r.getUser() != null ? r.getUser().getFirstName() : "Unknown User"
                ))
                .toList();
    }


}
