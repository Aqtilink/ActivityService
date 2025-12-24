package com.aqtilink.activity_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ActivityAlreadyStartedException extends RuntimeException {

    public ActivityAlreadyStartedException() {
        super("Activity already started");
    }
}
