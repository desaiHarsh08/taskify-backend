package com.taskify.common.exceptions;

import com.taskify.common.constants.ResourceType;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(ResourceType resourceType, String column, Object value, boolean isTemplate) {
        super(
                resourceType.name() +
                        (isTemplate ? "_TEMPLATE" : " ") +
                        "you are trying to access for " +
                        column + " (" + value + ")" +
                        "doesn't exist!"
        );
    }

}