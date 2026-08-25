package com.project.soul.user.application.exception;

public class FollowAlreadyExistsException extends RuntimeException {
    public FollowAlreadyExistsException(String message) {
        super(message);
    }
}
