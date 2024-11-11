package com.taskify.common.utils;

import com.taskify.common.constants.ResourceType;
import com.taskify.common.constants.SortingType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class Helper {

    public static final int PAGE_SIZE = 100;

    public static String getNotFoundMessage(ResourceType resourceType, String column, Object value, boolean isTemplate) {
        return resourceType.name() +
                (isTemplate ? "_TEMPLATE" : " ") +
                "you are trying to access for " +
                column + " (" + value + ")" +
                "doesn't exist!";
    }

    public static Pageable getPageable(int pageNumber) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("Page number should always be greater than 0.");
        }

        return PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));
    }

    public static Pageable getPageable(int pageNumber, Integer pageSize) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("Page number should always be greater than 0.");
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = PAGE_SIZE;
        }

        return PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
    }

    public static Pageable getPageable(
            int pageNumber,
            Integer pageSize,
            SortingType sortingType,
            String column
    ) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("Page number should always be greater than 0.");
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = PAGE_SIZE;
        }

        // Default sort order to descending if no sortingType or column is provided
        Sort sort = Sort.by(Sort.Direction.DESC, column != null && !column.isEmpty() ? column : "id");
        if (sortingType != null && column != null && !column.isEmpty()) {
            sort = sortingType == SortingType.ASC ? Sort.by(Sort.Direction.ASC, column) : Sort.by(Sort.Direction.DESC, column);
        }

        return PageRequest.of(pageNumber - 1, pageSize, sort);
    }

}
