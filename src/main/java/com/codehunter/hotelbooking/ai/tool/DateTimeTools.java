package com.codehunter.hotelbooking.ai.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@Slf4j
public class DateTimeTools {
    @Tool(description = "Provide the current date and time")
    String getCurrentDateTime() {
        log.info("Getting current date and time with timezone: {}", LocaleContextHolder.getTimeZone().toZoneId());
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

    @Tool(description = "Convert an Instant to a ZonedDateTime string in the specified timezone")
    String convertInstantToZoneDateTime(Instant instant, String zoneId) {
        log.info("Converting instant {} to zone date time with zoneId: {}", instant, zoneId);
        return instant.atZone(java.time.ZoneId.of(zoneId)).toString();
    }

    @Tool(description = "Convert an epoch time in milliseconds to a ZonedDateTime string in the specified timezone")
    String convertMillisecondsToZoneDateTime(Long milliseconds, String zoneId) {
        log.info("Converting milliseconds {} to zone date time with zoneId: {}", milliseconds,  zoneId);
        return Instant.ofEpochMilli(milliseconds).atZone(java.time.ZoneId.of(zoneId)).toString();
    }
}
