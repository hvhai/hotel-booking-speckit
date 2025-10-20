package com.codehunter.hotelbooking;

import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner ingestTermOfServiceToVectorStore(VectorStore vectorStore,
                                                       @Value("classpath:rag/hotel-booking-service-terms-of-use.txt") Resource termsOfServiceDocs) {

        return args -> vectorStore.write(
                new TokenTextSplitter().transform(
                        new TextReader(termsOfServiceDocs).read()));
    }

    @Bean
    RestClientCustomizer logbookCustomizer(
            LogbookClientHttpRequestInterceptor interceptor) {
        return restClient -> restClient.requestInterceptor(interceptor);
    }


}
