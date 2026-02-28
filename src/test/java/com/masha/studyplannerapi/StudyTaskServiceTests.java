package com.masha.studyplannerapi;

import com.masha.studyplannerapi.domain.entity.StudyTask;
import com.masha.studyplannerapi.domain.enums.StudyStatus;
import com.masha.studyplannerapi.exception.TaskNotFoundException;
import com.masha.studyplannerapi.repository.StudyTaskRepository;
import com.masha.studyplannerapi.service.StudyTaskService;
import com.masha.studyplannerapi.web.dto.CreateTaskRequest;
import com.masha.studyplannerapi.web.dto.TaskResponse;
import com.masha.studyplannerapi.web.dto.UpdateTaskRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudyTaskServiceTests {
    @Mock
    private StudyTaskRepository taskRepository;

    @InjectMocks
    private StudyTaskService taskService;

    @Test
    void create_validTask() {
        LocalDate dueDate = LocalDate.now();
        CreateTaskRequest taskRequest = new CreateTaskRequest("Math", "", "", dueDate);

        StudyTask saved = new StudyTask();
        saved.setId(1L);
        saved.setTitle("Math");
        saved.setDueDate(dueDate);
        saved.setStatus(StudyStatus.TO_DO);

        when(taskRepository.save(any(StudyTask.class))).thenReturn(saved);

        TaskResponse result = taskService.create(taskRequest);

        assertEquals(1L, result.id());
        assertEquals("Math", result.title());
        assertNull(result.description());
        assertNull(result.tag());
        assertEquals(dueDate, result.dueDate());
        assertEquals(StudyStatus.TO_DO, result.status());

        verify(taskRepository).save(any(StudyTask.class));
    }

    @Test
    void getById_validId() {
        LocalDate dueDate = LocalDate.now();
        Long id = 1L;
        StudyTask saved = new StudyTask();
        saved.setId(id);
        saved.setTitle("Math");
        saved.setDueDate(dueDate);
        saved.setStatus(StudyStatus.TO_DO);

        when(taskRepository.findById(id)).thenReturn(Optional.of(saved));

        TaskResponse result = taskService.getById(id);
        assertEquals(id, result.id());
        assertEquals("Math", result.title());
        assertNull(result.description());
        assertNull(result.tag());
        assertEquals(dueDate, result.dueDate());
        assertEquals(StudyStatus.TO_DO, result.status());

        verify(taskRepository).findById(id);
    }

    @Test
    void getById_invalidId() {
        Long id = -1L;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getById(id));
        verify(taskRepository).findById(id);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void update_validId() {
        LocalDate dueDate = LocalDate.now();
        UpdateTaskRequest taskRequest = new UpdateTaskRequest("Math", "", "", dueDate, StudyStatus.DONE);
        StudyTask saved = new StudyTask();
        saved.setId(1L);
        saved.setTitle("Math");
        saved.setDueDate(dueDate);
        saved.setStatus(StudyStatus.DONE);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(saved));
        when(taskRepository.save(any(StudyTask.class))).thenReturn(saved);

        TaskResponse result = taskService.update(1L, taskRequest);

        assertEquals(1L, result.id());
        assertEquals("Math", result.title());
        assertNull(result.description());
        assertNull(result.tag());
        assertEquals(dueDate, result.dueDate());
        assertEquals(StudyStatus.DONE, result.status());

        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any());
    }

    @Test
    void update_invalidId() {
        LocalDate dueDate = LocalDate.now();
        Long id = -1L;
        UpdateTaskRequest taskRequest = new UpdateTaskRequest("Math", "", "", dueDate, StudyStatus.DONE);

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.update(id, taskRequest));
        verify(taskRepository).findById(id);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void delete_validId() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteById(1L);

        verify(taskRepository).existsById(1L);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void delete_invalidId() {
        Long id = -1L;
        when(taskRepository.existsById(id)).thenReturn(false);
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteById(id));
        verify(taskRepository).existsById(id);
        verifyNoMoreInteractions(taskRepository);
    }
}
