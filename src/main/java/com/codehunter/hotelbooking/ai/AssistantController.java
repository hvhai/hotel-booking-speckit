package com.codehunter.hotelbooking.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/assistant")
@RequiredArgsConstructor
public class AssistantController {
    private final AssistantService assistantService;

    @PostMapping("/ask")
    public Answer askQuestion(@RequestBody Question question, @AuthenticationPrincipal User user) {
        return assistantService.askQuestion(question, user.getUsername());
    }

    @PostMapping("/stream/ask")
    public Flux<String> askStreamQuestion(@RequestBody Question question,
                                          @AuthenticationPrincipal User user) {
        return assistantService.streamQuestion(question, user.getUsername());
    }

    @GetMapping("/history/{chatId}")
    public java.util.List<org.springframework.ai.chat.messages.Message> getChatHistory(@org.springframework.web.bind.annotation.PathVariable String chatId) {
        return assistantService.fetchChatHistory(chatId);
    }

}
