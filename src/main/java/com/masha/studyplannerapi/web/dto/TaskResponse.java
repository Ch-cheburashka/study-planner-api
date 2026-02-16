package com.masha.studyplannerapi.web.dto;

import com.masha.studyplannerapi.domain.enums.StudyStatus;
import java.time.LocalDate;

public record TaskResponse(Long id, String title, String description, String tag, LocalDate dueDate, StudyStatus status) {
}
