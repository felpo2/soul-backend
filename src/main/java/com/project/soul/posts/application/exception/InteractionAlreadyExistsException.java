package com.project.soul.posts.application.exception;

public class InteractionAlreadyExistsException extends RuntimeException {

    public InteractionAlreadyExistsException(String message) {
        super(message);
    }
}