package com.codehunter.hotelbooking.ai;

import lombok.RequiredArgsConstructor;
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
    public Answer askQuestion(@RequestBody Question question) {
        return assistantService.askQuestion(question);
    }

    @PostMapping("/stream/ask")
    public Flux<String> askStreamQuestion(@RequestBody Question question) {
        return assistantService.streamQuestion(question);
    }

    @GetMapping("/history/{chatId}")
    public java.util.List<org.springframework.ai.chat.messages.Message> getChatHistory(@org.springframework.web.bind.annotation.PathVariable String chatId) {
        return assistantService.fetchChatHistory(chatId);
    }

}
