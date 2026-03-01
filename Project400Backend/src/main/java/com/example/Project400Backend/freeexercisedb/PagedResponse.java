package com.example.Project400Backend.freeexercisedb;

import java.util.List;

public record PagedResponse<T>(
        long count,
        int page,
        int limit,
        List<T> results
) {}