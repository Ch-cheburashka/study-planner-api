package com.masha.studyplannerapi.web.controller;

import com.masha.studyplannerapi.service.StudyTaskService;
import com.masha.studyplannerapi.web.dto.CreateTaskRequest;
import com.masha.studyplannerapi.web.dto.TaskResponse;
import com.masha.studyplannerapi.web.dto.UpdateTaskRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class StudyTaskController {
    private final StudyTaskService studyTaskService;
    public StudyTaskController(StudyTaskService studyTaskService) {
        this.studyTaskService = studyTaskService;
    }

    @PostMapping({"", "/"})
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest createTaskRequest) {
        return studyTaskService.create(createTaskRequest);
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable Long id) {
        return studyTaskService.getById(id);
    }

    @GetMapping({"", "/"})
    public List<TaskResponse> getAll() {
        return studyTaskService.getAll();
    }

    @PutMapping("/{id}")
    public TaskResponse update(@Valid @RequestBody UpdateTaskRequest updateTaskRequest, @PathVariable Long id) {
        return studyTaskService.update(id, updateTaskRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        studyTaskService.deleteById(id);
    }
}
