package com.codehunter.hotelbooking.ai.config;

import com.codehunter.hotelbooking.ai.copilot.CopilotTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
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
    public ChatClient chatClient(ChatMemory chatMemory, OpenAiChatModel openAiChatModel) {
        ChatOptions chatOptions = ChatOptions.builder()
                .model("gpt-4.1")
                .temperature(0.7)
                .maxTokens(3000)
                .build();
        return ChatClient.create(openAiChatModel)
                .mutate()
                .defaultOptions(chatOptions)
                .defaultSystem("You are a helpful Java assistant.")
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build())
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
    public OpenAiChatModel copilotOpenAiChatModel(OpenAiApi openAiApi ) {
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
}
