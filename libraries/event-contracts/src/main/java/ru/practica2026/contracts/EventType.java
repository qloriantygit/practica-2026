package ru.practica2026.contracts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum EventType {

    DIRECTORY_UPDATED("DirectoryUpdated"),

    ROLE_CHANGED("RoleChanged"),

    EXPERT_PROFILE_UPDATED("ExpertProfileUpdated"),

    NOTIFICATION_REQUESTED("NotificationRequested");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EventType fromValue(String value) {

        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Unknown event type: " + value
                        )
                );
    }
}
