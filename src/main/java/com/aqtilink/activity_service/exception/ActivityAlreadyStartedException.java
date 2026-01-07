package com.aqtilink.activity_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Exception thrown when an activity has already been started and user tries to join it

@ResponseStatus(HttpStatus.CONFLICT)
public class ActivityAlreadyStartedException extends RuntimeException {

    public ActivityAlreadyStartedException() {
        super("Activity already started");
    }
}
