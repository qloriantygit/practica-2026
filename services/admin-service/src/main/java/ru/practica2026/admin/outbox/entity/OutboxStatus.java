package ru.practica2026.admin.outbox.entity;

public enum OutboxStatus {

    PENDING,

    FAILED,

    SENT,

    DEAD
}
