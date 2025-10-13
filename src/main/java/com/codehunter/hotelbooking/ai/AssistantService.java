package com.codehunter.hotelbooking.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class AssistantService {
    private final ChatClient chatClient;

    public Answer askQuestion(Question question) {
        String response = chatClient.prompt()
                .user(question.question())
                .call()
                .content();
        return new Answer(response);
    }

    public Flux<String> streamQuestion(Question question) {
        return chatClient.prompt()
                        .user(question.question())
                .stream()
                .content();
    }

}
