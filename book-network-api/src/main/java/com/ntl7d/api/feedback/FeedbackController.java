package com.ntl7d.api.feedback;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping()
    public ResponseEntity<String> saveFeedback(@RequestBody @Valid FeedbackRequest request,
            Authentication connectedUser) {
        return ResponseEntity.ok(feedbackService.saveFeedback(request, connectedUser));
    }
}
