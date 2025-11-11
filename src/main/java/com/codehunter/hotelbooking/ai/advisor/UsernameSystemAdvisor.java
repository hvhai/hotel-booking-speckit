package com.codehunter.hotelbooking.ai.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;

public class UsernameSystemAdvisor implements BaseAdvisor {

    public static final String USERNAME_PARAM = "username";
    private static final int DEFAULT_ORDER = 0;

    private final int order;
    private final String username;

    public UsernameSystemAdvisor(String username) {
        this(username, DEFAULT_ORDER);
    }

    public UsernameSystemAdvisor(String username, int order) {
        this.order = order;
        this.username = username;
    }


    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        if (username != null && !username.isBlank()) {
            // Append username to system message
            String usernameSystemText = "\nThe current user is " + username;

            // Get the current prompt
            Prompt originalPrompt = chatClientRequest.prompt();
            List<Message> messages = new ArrayList<>(originalPrompt.getInstructions());

            // Find existing system message and append to it, or create a new one
            boolean systemMessageFound = false;
            for (int i = 0; i < messages.size(); i++) {
                Message msg = messages.get(i);
                if (msg instanceof SystemMessage systemMessage) {
                    // Append to existing system message
                    String existingContent = systemMessage.getText();
                    SystemMessage updatedSystemMessage = new SystemMessage(existingContent + usernameSystemText);
                    messages.set(i, updatedSystemMessage);
                    systemMessageFound = true;
                    break;
                }
            }

            // If no system message found, add one at the beginning
            if (!systemMessageFound) {
                messages.add(0, new SystemMessage(usernameSystemText));
            }

            // Create new prompt with updated messages
            Prompt updatedPrompt = new Prompt(messages, originalPrompt.getOptions());

            // Create new chat client request with updated prompt
            return chatClientRequest.mutate()
                    .prompt(updatedPrompt)
                    .build();
        }

        // If no username provided, just continue with original request
        return chatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        // No post-processing needed, just return the response as is
        return chatClientResponse;
    }

    @Override
    public Scheduler getScheduler() {
        return BaseAdvisor.super.getScheduler();
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
