package com.masha.studyplannerapi.service;

import com.masha.studyplannerapi.domain.entity.StudyTask;
import com.masha.studyplannerapi.domain.enums.StudyStatus;
import com.masha.studyplannerapi.repository.StudyTaskRepository;
import com.masha.studyplannerapi.exception.TaskNotFoundException;
import com.masha.studyplannerapi.web.dto.CreateTaskRequest;
import com.masha.studyplannerapi.web.dto.TaskResponse;
import com.masha.studyplannerapi.web.dto.UpdateTaskRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyTaskService {
    private final StudyTaskRepository taskRepository;
    public StudyTaskService(StudyTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    private TaskResponse toTaskResponse(StudyTask studyTask) {
        return new TaskResponse(studyTask.getId(), studyTask.getTitle(), studyTask.getDescription(), studyTask.getTag(), studyTask.getDueDate(), studyTask.getStatus());
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
    private String normalizeRequiredText(String value) {
        return value.trim();
    }

    public TaskResponse create(CreateTaskRequest createTaskRequest) {
        StudyTask studyTask = new StudyTask(
                normalizeRequiredText(createTaskRequest.title()), normalizeOptionalText(createTaskRequest.description()),
                normalizeOptionalText(createTaskRequest.tag()), createTaskRequest.dueDate(), StudyStatus.TO_DO);
        StudyTask saved = taskRepository.save(studyTask);
        return toTaskResponse(saved);
    }
    public TaskResponse getById(Long id) {
        StudyTask studyTask = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        return toTaskResponse(studyTask);
    }
    public List<TaskResponse> getAll() {
        return taskRepository.findAll().stream().map(this::toTaskResponse).toList();
    }
    public TaskResponse update(Long id, UpdateTaskRequest updateTaskRequest) {
        StudyTask studyTask = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));

        studyTask.setTitle(normalizeRequiredText(updateTaskRequest.title()));
        studyTask.setDescription(normalizeOptionalText(updateTaskRequest.description()));
        studyTask.setTag(normalizeOptionalText(updateTaskRequest.tag()));
        studyTask.setDueDate(updateTaskRequest.dueDate());
        studyTask.setStatus(updateTaskRequest.status());

        StudyTask saved = taskRepository.save(studyTask);
        return toTaskResponse(saved);
    }
    public void deleteById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }

    public List<TaskResponse> searchTasks(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        return taskRepository.searchTasks(keyword.trim()).stream().map(this::toTaskResponse).toList();
    }
}
