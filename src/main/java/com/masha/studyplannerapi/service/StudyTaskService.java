package com.masha.studyplannerapi.service;

import com.masha.studyplannerapi.domain.entity.StudyTask;
import com.masha.studyplannerapi.domain.enums.StudyStatus;
import com.masha.studyplannerapi.repository.StudyTaskRepository;
import com.masha.studyplannerapi.exception.TaskNotFoundException;
import com.masha.studyplannerapi.web.dto.CreateTaskRequest;
import com.masha.studyplannerapi.web.dto.TaskResponse;
import com.masha.studyplannerapi.web.dto.UpdateTaskRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public TaskResponse create(CreateTaskRequest createTaskRequest) {
        StudyTask studyTask = new StudyTask(createTaskRequest.title(), createTaskRequest.description(),
                createTaskRequest.tag(), createTaskRequest.dueDate(), StudyStatus.TO_DO);
        taskRepository.save(studyTask);
        return toTaskResponse(studyTask);
    }
    public TaskResponse getById(Long id) {
        StudyTask studyTask = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        return toTaskResponse(studyTask);
    }
    public List<TaskResponse> getAll() {
        List<StudyTask> studyTasks = taskRepository.findAll();
        List<TaskResponse> taskResponses = new ArrayList<>();
        for (StudyTask studyTask : studyTasks) {
            taskResponses.add(toTaskResponse(studyTask));
        }
        return taskResponses;
    }
    public TaskResponse update(Long id, UpdateTaskRequest updateTaskRequest) {
        StudyTask studyTask = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));

        studyTask.setTitle(updateTaskRequest.title());
        studyTask.setDescription(updateTaskRequest.description());
        studyTask.setTag(updateTaskRequest.tag());
        studyTask.setDueDate(updateTaskRequest.dueDate());
        studyTask.setStatus(updateTaskRequest.status());

        taskRepository.save(studyTask);
        return toTaskResponse(studyTask);
    }
    public void deleteById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }
}
