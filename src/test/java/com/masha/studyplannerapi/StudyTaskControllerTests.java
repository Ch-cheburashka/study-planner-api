package com.masha.studyplannerapi;

import com.masha.studyplannerapi.domain.entity.StudyTask;
import com.masha.studyplannerapi.domain.enums.StudyStatus;
import com.masha.studyplannerapi.exception.TaskNotFoundException;
import com.masha.studyplannerapi.service.StudyTaskService;
import com.masha.studyplannerapi.web.controller.StudyTaskController;
import com.masha.studyplannerapi.web.dto.CreateTaskRequest;
import com.masha.studyplannerapi.web.dto.TaskResponse;
import com.masha.studyplannerapi.web.dto.UpdateTaskRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

@WebMvcTest(StudyTaskController.class)
public class StudyTaskControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudyTaskService studyTaskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class CreateTaskRequestTest {
        @Test
        void shouldCreateTaskAndReturn201AndValidateInput() throws Exception {
            LocalDate dueDate = LocalDate.of(2026, 9, 1);
            CreateTaskRequest mockRequest = new CreateTaskRequest("Clean", "", "", dueDate);
            TaskResponse mockResponse = new TaskResponse(1L, "Clean", "", "", dueDate, StudyStatus.TO_DO);
            when(studyTaskService.create(any(CreateTaskRequest.class))).thenReturn(mockResponse);
            mockMvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockRequest))
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isCreated())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.id").value(1))
                            .andExpect(jsonPath("$.title").value("Clean"));
        }

        @Test
        void shouldReturn400WhenInputIsInvalid() throws Exception {
            LocalDate dueDate = LocalDate.of(2026, 9, 1);
            CreateTaskRequest mockRequest = new CreateTaskRequest(" ", "", "", dueDate);
            mockMvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockRequest))
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.message").value("Validation failed"))
                            .andExpect(jsonPath("$.fieldErrors.title").exists());
        }
    }

    @Nested
    class GetTaskByIdTest {
        @Test
        public void shouldGetTaskByIdAndReturn200AndJson() throws Exception {
            LocalDate dueDate = LocalDate.of(2026, 9, 1);
            TaskResponse mockResponse = new TaskResponse(1L, "Clean", "", "", dueDate, StudyStatus.TO_DO);
            when(studyTaskService.getById(1L)).thenReturn(mockResponse);
            mockMvc.perform(get("/api/tasks/1").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Clean"));
        }

        @Test
        public void shouldReturn404WhenTaskNotFound() throws Exception {
            when(studyTaskService.getById(99L)).thenThrow(new TaskNotFoundException(99L));
            mockMvc.perform(get("/api/tasks/99").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("Task with id 99 not found"));
        }
    }

    @Nested
    class GetAllTasksTest {
        @Test
        void shouldReturn200AndListOfTasks() throws Exception {
            LocalDate dueDate = LocalDate.of(2026, 9, 1);
            TaskResponse task1 = new TaskResponse(1L, "Clean", "", "", dueDate, StudyStatus.TO_DO);
            TaskResponse task2 = new TaskResponse(2L, "Study", "", "", dueDate, StudyStatus.IN_PROGRESS);

            when(studyTaskService.getAll()).thenReturn(List.of(task1, task2));

            mockMvc.perform(get("/api/tasks").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.size()").value(2))
                    .andExpect(jsonPath("$[0].title").value("Clean"))
                    .andExpect(jsonPath("$[1].title").value("Study"));
        }
    }

    @Nested
    class UpdateTaskTest {
        @Test
        void shouldUpdateTaskAndReturn200() throws Exception {
            LocalDate dueDate = LocalDate.of(2026, 9, 1);
            UpdateTaskRequest mockRequest = new UpdateTaskRequest("Clean Updated", "", "", dueDate, StudyStatus.DONE);
            TaskResponse mockResponse = new TaskResponse(1L, "Clean Updated", "", "", dueDate, StudyStatus.DONE);

            when(studyTaskService.update(eq(1L), any(UpdateTaskRequest.class))).thenReturn(mockResponse);

            mockMvc.perform(put("/api/tasks/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockRequest))
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.title").value("Clean Updated"))
                            .andExpect(jsonPath("$.status").value("DONE"));
        }

        @Test
        void shouldReturn404WhenUpdatingNonExistentTask() throws Exception {
            LocalDate dueDate = LocalDate.of(2026, 9, 1);
            UpdateTaskRequest mockRequest = new UpdateTaskRequest("Clean", "", "", dueDate, StudyStatus.TO_DO);

            when(studyTaskService.update(eq(99L), any(UpdateTaskRequest.class))).thenThrow(new TaskNotFoundException(99L));

            mockMvc.perform(put("/api/tasks/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockRequest))
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isNotFound())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.message").value("Task with id 99 not found"));
        }
    }

    @Nested
    class DeleteTaskTest {
        @Test
        void shouldDeleteTaskAndReturn204() throws Exception {
            doNothing().when(studyTaskService).deleteById(1L);

            mockMvc.perform(delete("/api/tasks/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturn404WhenDeletingNonExistentTask() throws Exception {
            doThrow(new TaskNotFoundException(99L)).when(studyTaskService).deleteById(99L);

            mockMvc.perform(delete("/api/tasks/99").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("Task with id 99 not found"));
        }
    }

    @Nested
    class SearchTasksTest {
        @Test
        void shouldReturn200AndFilteredTasks() throws Exception {
            LocalDate dueDate = LocalDate.of(2026, 9, 1);
            TaskResponse mockResponse = new TaskResponse(1L, "Clean", "", "", dueDate, StudyStatus.TO_DO);

            when(studyTaskService.searchTasks("Clean")).thenReturn(List.of(mockResponse));

            mockMvc.perform(get("/api/tasks/search/Clean").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.size()").value(1))
                    .andExpect(jsonPath("$[0].title").value("Clean"));
        }
    }
}
