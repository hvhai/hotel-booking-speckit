package com.codehunter.hotelbooking.ai;

import com.codehunter.hotelbooking.ai.tool.BookingTools;
import com.codehunter.hotelbooking.ai.tool.DateTimeTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class AssistantService {
    private final ChatClient chatClient;
    private final BookingTools bookingTools;
    private final DateTimeTools dateTimeTools;

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
                .tools(dateTimeTools, bookingTools)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, question.chatId()))
                .stream()
                .content();
    }

}
