package com.masha.studyplannerapi.web.dto;

import com.masha.studyplannerapi.domain.enums.StudyStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateTaskRequest(
        @NotBlank(message = "title must be not empty") String title,
        String description,
        String tag,
        @FutureOrPresent LocalDate dueDate,
        @NotNull StudyStatus status
) {}
