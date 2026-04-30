package com.example.eco.RESTController;

import com.example.eco.publisher.MessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    @Autowired
    private MessagePublisher messagePublisher;

    @Autowired
    private RestTemplate restTemplate;

    private final String AUTH_URL = "http://localhost:3000/auth/verify-token";

    @PostMapping
    public ResponseEntity<?> publishMessage(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody String jsonBody) {

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", "error",
                            "message", "Authorization token is required."
                    ));
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(AUTH_URL, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String authMessage = (String) response.getBody().get("message");

                if ("JWT Valid".equals(authMessage)) {
                    messagePublisher.sendMessage(jsonBody);

                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(Map.of(
                                    "status", "success",
                                    "message", "Message successfully published to 2 Kafka topics.",
                                    "verifiedUser", response.getBody().get("user")
                            ));
                }
            }

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(Map.class));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Auth service unavailable: " + e.getMessage()
                    ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "status", "error",
                        "message", "Invalid or expired token."
                ));
    }
}