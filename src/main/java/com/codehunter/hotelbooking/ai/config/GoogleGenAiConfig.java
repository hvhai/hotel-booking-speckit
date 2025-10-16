package com.codehunter.hotelbooking.ai.config;

import com.google.genai.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiEmbeddingConnectionDetails;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingModel;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Slf4j
public class GoogleGenAiConfig {
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(5)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatMemory chatMemory,
                                 GoogleGenAiChatModel googleGenAiChatModel,
                                 VectorStore vectorStore) {
        return ChatClient.create(googleGenAiChatModel)
                .mutate()
                .defaultSystem("""
                        You are a customer chat support agent of Hotel named "18 plus"."
                        Respond in a friendly, helpful, and joyful manner.
                        You are interacting with customers through an online chat system.
                        Before answering a question about a booking or cancelling a booking, you MUST always
                        get the following information from the user: booking number, customer email.
                        If you can not retrieve the status of my booking, please just say "I am sorry, I can not find the booking details".
                        Check the message history for booking details before asking the user.
                        Before changing a booking you MUST ensure it is permitted by the terms.
                        If there is a charge for the change, you MUST ask the user to consent before proceeding.
                        Use the provided functions to fetch booking details, change bookings, and cancel bookings.
                        """)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore).build())
                .build();
    }

    @Bean
    @Primary
    GoogleGenAiChatModel googleGenAiChatModel(@Value("${spring.ai.google.genai.api-key}") String apiKey,
                                              @Value("${spring.ai.google.genai.model}") String model,
                                              @Value("${spring.ai.google.genai.temperature}") double temperature) {
        Client genAiClient = Client.builder()
                .apiKey(apiKey)
                .build();
        return GoogleGenAiChatModel.builder()
                .genAiClient(genAiClient)
                .defaultOptions(org.springframework.ai.google.genai.GoogleGenAiChatOptions.builder()
                        .model(model)
                        .temperature(temperature)
                        .build())
                .build();
    }

    @Bean
    public GoogleGenAiEmbeddingConnectionDetails googleGenAiEmbeddingConnectionDetails(@Value("${spring.ai.google.genai.api-key}") String apiKey,
                                                                                 @Value("${spring.ai.google.genai.project-id}") String projectId,
                                                                                 @Value("${spring.ai.google.genai.location:us-central1}") String location) {
        return GoogleGenAiEmbeddingConnectionDetails.builder()
                .projectId(projectId)
                .location(location)
                .apiKey(apiKey)
                .build();
    }

    @Bean
    @Primary
    public GoogleGenAiTextEmbeddingModel googleGenAiTextEmbeddingModel(GoogleGenAiEmbeddingConnectionDetails googleGenAiEmbeddingConnectionDetails) {
        GoogleGenAiTextEmbeddingOptions options = GoogleGenAiTextEmbeddingOptions.builder()
                .model(GoogleGenAiTextEmbeddingOptions.DEFAULT_MODEL_NAME)
                .taskType(GoogleGenAiTextEmbeddingOptions.TaskType.RETRIEVAL_DOCUMENT)
                .build();

        return new GoogleGenAiTextEmbeddingModel(googleGenAiEmbeddingConnectionDetails, options);
    }
}
