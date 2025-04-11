package com.aldenleon.treeter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No comment matches id")
public class CommentNotFoundException extends RuntimeException {
}
