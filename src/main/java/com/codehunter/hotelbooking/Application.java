package com.codehunter.hotelbooking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

import java.util.List;

@SpringBootApplication
@Slf4j
public class Application {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner ingestTermOfServiceToVectorStore(VectorStore vectorStore,
                                                       @Value("classpath:rag/hotel-booking-service-terms-of-use.txt") Resource termsOfServiceDocs) {

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query("Terms of Service")
                .similarityThreshold(0.45)
                .build());
        if (documents.isEmpty()) {
            return args -> vectorStore.write(
                    new TokenTextSplitter().transform(
                            new TextReader(termsOfServiceDocs).read()));
        }
        return args -> {
            log.info("terms-of-use.txt already ingested to vector store, skipping ingestion");
        };
    }

    @Bean
    RestClientCustomizer logbookCustomizer(
            LogbookClientHttpRequestInterceptor interceptor) {
        return restClient -> restClient.requestInterceptor(interceptor);
    }


}
