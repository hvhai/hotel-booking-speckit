package com.codehunter.hotelbooking.ai.config;

import com.codehunter.hotelbooking.ai.tool.CalculationTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
public class OpenAiConfig {
    @Bean
    @Profile("openai")
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(50)
                .build();
    }

    @Profile("openai")
    @Bean
    public ChatClient chatClient(ChatMemory chatMemory,
                                 OpenAiChatModel openAiChatModel,
                                 VectorStore vectorStore, CalculationTools calculationTools) {
        return ChatClient.create(openAiChatModel)
                .mutate()
                .defaultTools(calculationTools)
                .defaultSystem("""
                        You are a customer chat support agent of Hotel named "18 plus"."
                        Respond in a friendly, helpful, and joyful manner.
                        You are interacting with customers through an online chat system.
                        Before answering a question about a booking or cancelling a booking, you MUST always
                        get the following information from the user: booking number.
                        If you can not retrieve the status of my booking, please just say "I am sorry, I can not find the booking details".
                        Check the message history for booking details before asking the user.
                        Before changing a booking you MUST ensure it is permitted by the terms.
                        If there is a charge for the change, you MUST ask the user to consent before proceeding.
                        Use the provided functions to fetch booking details, change bookings, and cancel bookings.
                        """)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        PromptChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore).build())
                .build();
    }

}
