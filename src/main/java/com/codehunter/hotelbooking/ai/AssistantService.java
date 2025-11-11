package com.codehunter.hotelbooking.ai;

import com.codehunter.hotelbooking.ai.advisor.UsernameSystemAdvisor;
import com.codehunter.hotelbooking.ai.tool.BookingTools;
import com.codehunter.hotelbooking.ai.tool.DateTimeTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssistantService {
    private final ChatClient chatClient;
    private final BookingTools bookingTools;
    private final DateTimeTools dateTimeTools;
    private final ChatMemory chatMemory;

    public Answer askQuestion(Question question, String username) {
        String response = chatClient.prompt()
                .user(question.question())
                .advisors(advisorSpec -> advisorSpec
                        .param(ChatMemory.CONVERSATION_ID, question.chatId())
                        .advisors(new UsernameSystemAdvisor(username)))
                .call()
                .content();
        return new Answer(response);
    }

    public Flux<String> streamQuestion(Question question, String username) {
        return chatClient.prompt()
                .user(question.question())
                .tools(dateTimeTools, bookingTools)
                .advisors(advisorSpec -> advisorSpec
                        .param(ChatMemory.CONVERSATION_ID, question.chatId())
                        .advisors(new UsernameSystemAdvisor(username)))
                .stream()
                .content();
    }

    // fetching chat history from ChatMemory based on chatId can be added here if needed
    public List<Message> fetchChatHistory(String chatId) {
        // Implementation for fetching chat history
        List<Message> messages = chatMemory.get(chatId);
        return messages;


    }
}
