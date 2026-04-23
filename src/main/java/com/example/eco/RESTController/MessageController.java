package com.example.eco.RESTController;

import com.example.eco.publisher.MessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private MessagePublisher messagePublisher;

    @Autowired
    private RestTemplate restTemplate;

    // URL Service Auth untuk verifikasi token (Node.js Service)
    private final String AUTH_URL = "http://localhost:3000/auth/verify-token";

    @PostMapping("/publish")
    public ResponseEntity<?> publishMessage(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody String jsonBody) {

        // 1. Validasi keberadaan token di Header Authorization
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Token diperlukan."));
        }

        try {
            // 2. Siapkan Header untuk memanggil API verifikasi di Node.js
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 3. Panggil API External (Auth Service)
            ResponseEntity<Map> response = restTemplate.exchange(AUTH_URL, HttpMethod.POST, entity, Map.class);

            // 4. Cek respons dari Auth Service
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String authMessage = (String) response.getBody().get("message");

                if ("JWT Valid".equals(authMessage)) {
                    // 5. Kirim pesan ke RabbitMQ (CloudAMQP) melalui MessagePublisher
                    messagePublisher.sendMessage(jsonBody);

                    return ResponseEntity.ok(Map.of(
                            "status", "success",
                            "message", "Pesan berhasil dikirim ke 2 channel CloudAMQP",
                            "verifiedUser", response.getBody().get("user")
                    ));
                }
            }

        } catch (HttpClientErrorException e) {
            // Menangkap pesan error spesifik: "Token tidak valid atau sudah kadaluwarsa."
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(Map.class));
        } catch (Exception e) {
            // Menangkap error jika Auth Service (Node.js) sedang down
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Auth Service Error: " + e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Token tidak valid."));
    }
}