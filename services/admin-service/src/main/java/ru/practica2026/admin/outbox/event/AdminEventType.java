package ru.practica2026.admin.outbox.event;

public enum AdminEventType {

    DIRECTORY_UPDATED(
            "DirectoryUpdated",
            "admin.directory.updated"
    ),

    ROLE_CHANGED(
            "RoleChanged",
            "admin.role.changed"
    ),

    EXPERT_PROFILE_UPDATED(
            "ExpertProfileUpdated",
            "admin.expert.profile.updated"
    );

    private final String eventName;
    private final String routingKey;

    AdminEventType(
            String eventName,
            String routingKey
    ) {
        this.eventName = eventName;
        this.routingKey = routingKey;
    }

    public String getEventName() {
        return eventName;
    }

    public String getRoutingKey() {
        return routingKey;
    }
}
