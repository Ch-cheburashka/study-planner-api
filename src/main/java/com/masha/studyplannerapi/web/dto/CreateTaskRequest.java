package com.masha.studyplannerapi.web.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank(message = "title must be not empty") String title,
        String description,
        String tag,
        @FutureOrPresent LocalDate dueDate
) {
}
