package com.masha.studyplannerapi.web.controller;

import com.masha.studyplannerapi.service.StudyTaskService;
import com.masha.studyplannerapi.web.dto.CreateTaskRequest;
import com.masha.studyplannerapi.web.dto.TaskResponse;
import com.masha.studyplannerapi.web.dto.UpdateTaskRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class StudyTaskController {
    private final StudyTaskService studyTaskService;
    public StudyTaskController(StudyTaskService studyTaskService) {
        this.studyTaskService = studyTaskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest createTaskRequest) {
        TaskResponse createdTask = studyTaskService.create(createTaskRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdTask.id()).toUri();
        return ResponseEntity.created(location).body(createdTask);
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
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        studyTaskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/{keyword}")
    public List<TaskResponse> searchTasks(@PathVariable String keyword) {
        return studyTaskService.searchTasks(keyword);
    }
}
