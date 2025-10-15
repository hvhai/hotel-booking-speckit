package com.codehunter.hotelbooking.ai.config;

import com.codehunter.hotelbooking.ai.copilot.CopilotTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Configuration
@Slf4j
public class AiConfig {
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(5)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatMemory chatMemory,
                                 OpenAiChatModel openAiChatModel,
                                 VectorStore vectorStore) {
        ChatOptions chatOptions = ChatOptions.builder()
                .model("gpt-4.1")
                .temperature(0.7)
                .maxTokens(3000)
                .build();
        return ChatClient.create(openAiChatModel)
                .mutate()
                .defaultOptions(chatOptions)
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
    public OpenAiApi openAiApi(
            CopilotTokenService copilotTokenService,
            @Value("${app.ai.copilot.openai.chat.base-url}") String copilotBaseUrl,
            @Value("${app.ai.copilot.openai.chat.completions-path}") String copilotCompletionsPath,
            @Value("${app.ai.copilot.openai.chat.options.http-headers.Copilot-Integration-Id}") String copilotIntegrationId,
            @Value("${app.ai.copilot.openai.chat.options.http-headers.Editor-Plugin-Version}") String copilotEditorPluginVersion,
            @Value("${app.ai.copilot.openai.chat.options.http-headers.Editor-Version}") String copilotEditorVersion,
            @Value("${app.ai.copilot.openai.chat.options.http-headers.X-Initiator}") String copilotXInitiator
    ) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Copilot-Integration-Id", copilotIntegrationId);
        httpHeaders.add("Editor-Plugin-Version", copilotEditorPluginVersion);
        httpHeaders.add("Editor-Version", copilotEditorVersion);
        httpHeaders.add("X-Initiator", copilotXInitiator);
        httpHeaders.add("X-Request-Id", UUID.randomUUID().toString());
        return OpenAiApi.builder()
                .baseUrl(copilotBaseUrl)
                .completionsPath(copilotCompletionsPath)
                .restClientBuilder(RestClient.builder()
                        // Interceptor to add the latest token to each request
                        .requestInterceptor((request, body, execution) -> {
                            String latestToken = copilotTokenService.getToken();
                            log.info("Adding Authorization header for RestClient with latest token: {}", latestToken);
                            request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + latestToken);
                            return execution.execute(request, body);
                        })
                )
                .webClientBuilder(WebClient.builder()
                        // Interceptor to add the latest token to each request
                        .filter(addAuthorizationHeaders(new LinkedMultiValueMap<>(), copilotTokenService))
                )
                .apiKey(new NoopApiKey()) // Use NoopApiKey since we're handling auth via headers
                .headers(httpHeaders)
                .build();
    }

    @Bean
    public OpenAiChatModel copilotOpenAiChatModel(OpenAiApi openAiApi) {
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .build();
    }

    public ExchangeFilterFunction addAuthorizationHeaders(MultiValueMap<String, String> changedMap, CopilotTokenService copilotTokenService) {
        return (request, next) ->
                Mono.just(copilotTokenService.getToken())
                        .flatMap(token -> {
                            ClientRequest clientRequest = ClientRequest.from(request)
                                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                    .build();
                            log.info("Adding Authorization header for Webclient with latest token: {}", token);
                            changedMap.addAll(clientRequest.headers());
                            return next.exchange(clientRequest);
                        });
    }

    @Bean
    public OpenAiEmbeddingModel openAiEmbeddingModel(@Value("${spring.ai.openai.embedding.api-key}") String personalOpenAiApiKey) {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(personalOpenAiApiKey)
                .build();
        return new OpenAiEmbeddingModel(
                openAiApi,
                MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder().model("text-embedding-3-small").build(),
                RetryUtils.DEFAULT_RETRY_TEMPLATE
        );
    }
}
