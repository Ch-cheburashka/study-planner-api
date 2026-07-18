package com.masha.studyplannerapi;

import com.masha.studyplannerapi.domain.entity.StudyTask;
import com.masha.studyplannerapi.domain.enums.StudyStatus;
import com.masha.studyplannerapi.exception.TaskNotFoundException;
import com.masha.studyplannerapi.repository.StudyTaskRepository;
import com.masha.studyplannerapi.service.StudyTaskService;
import com.masha.studyplannerapi.web.dto.CreateTaskRequest;
import com.masha.studyplannerapi.web.dto.TaskResponse;
import com.masha.studyplannerapi.web.dto.UpdateTaskRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudyTaskServiceTests {
    @Mock
    private StudyTaskRepository studyTaskRepository;
    @InjectMocks
    private StudyTaskService studyTaskService;
    @Captor
    private ArgumentCaptor<StudyTask> studyTaskCaptor;

    @Nested class CreateStudyTaskTest {
        @Test
        void shouldCreateTaskWithDefaultStatusWhenInputIsValid() {
            LocalDate dueDate = LocalDate.of(2026, 9, 1);
            CreateTaskRequest request = new CreateTaskRequest("Analysis", "Exam prep", "uni", dueDate);

            when(studyTaskRepository.save(any(StudyTask.class))).thenAnswer(invocationOnMock -> {
                StudyTask taskToSave = invocationOnMock.getArgument(0);
                taskToSave.setId(1L);
                return taskToSave;
            });

            TaskResponse response = studyTaskService.create(request);

            verify(studyTaskRepository).save(studyTaskCaptor.capture());

            StudyTask savedTask = studyTaskCaptor.getValue();

            assertAll("saved study task",
                    () -> assertEquals("Analysis", savedTask.getTitle()),
                    () -> assertEquals("Exam prep", savedTask.getDescription()),
                    () -> assertEquals("uni", savedTask.getTag()),
                    () -> assertEquals(dueDate, savedTask.getDueDate()),
                    () -> assertEquals(StudyStatus.TO_DO, savedTask.getStatus())
            );

            assertAll("task response",
                    () -> assertEquals(1L, response.id()),
                    () -> assertEquals("Analysis", response.title()),
                    () -> assertEquals("Exam prep", response.description()),
                    () -> assertEquals("uni", response.tag()),
                    () -> assertEquals(dueDate, response.dueDate()),
                    () -> assertEquals(StudyStatus.TO_DO, response.status())
            );
        }
        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        void shouldNormalizeBlankDescriptionAndTagToNull(String blankValue) {
            LocalDate dueDate = LocalDate.of(2026, 9, 1);

            CreateTaskRequest request = new CreateTaskRequest(
                    "Analysis",
                    blankValue,
                    blankValue,
                    dueDate
            );

            when(studyTaskRepository.save(any(StudyTask.class)))
                    .thenAnswer(invocation -> {
                        StudyTask task = invocation.getArgument(0);
                        task.setId(1L);
                        return task;
                    });

            TaskResponse response = studyTaskService.create(request);

            verify(studyTaskRepository).save(studyTaskCaptor.capture());

            StudyTask savedTask = studyTaskCaptor.getValue();

            assertAll(
                    () -> assertNull(savedTask.getDescription()),
                    () -> assertNull(savedTask.getTag()),
                    () -> assertNull(response.description()),
                    () -> assertNull(response.tag())
            );
        }
        @Test
        void shouldTrimTextFieldsWhenCreatingTask() {
            LocalDate dueDate = LocalDate.of(2026, 9, 1);

            CreateTaskRequest request = new CreateTaskRequest(
                    "  Analysis  ",
                    "  Exam prep  ",
                    "  uni  ",
                    dueDate
            );

            when(studyTaskRepository.save(any(StudyTask.class)))
                    .thenAnswer(invocation -> {
                        StudyTask task = invocation.getArgument(0);
                        task.setId(1L);
                        return task;
                    });

            TaskResponse response = studyTaskService.create(request);

            verify(studyTaskRepository).save(studyTaskCaptor.capture());

            StudyTask savedTask = studyTaskCaptor.getValue();

            assertAll(
                    () -> assertEquals("Analysis", savedTask.getTitle()),
                    () -> assertEquals("Exam prep", savedTask.getDescription()),
                    () -> assertEquals("uni", savedTask.getTag()),
                    () -> assertEquals("Analysis", response.title()),
                    () -> assertEquals("Exam prep", response.description()),
                    () -> assertEquals("uni", response.tag())
            );
        }
    }

    @Nested class GetTaskByIdTest {
        @Test
        void shouldGetTaskByIdWhenTaskExists() {
            Long id = 1L;
            LocalDate dueDate = LocalDate.of(2026, 9, 1);
            StudyTask task = new StudyTask("Analysis", "Exam prep", "uni", dueDate, StudyStatus.TO_DO);
            task.setId(id);
            when(studyTaskRepository.findById(id)).thenReturn(Optional.of(task));

            TaskResponse response = studyTaskService.getById(id);

            verify(studyTaskRepository).findById(id);

            assertAll(
                    () -> assertEquals(id, response.id()),
                    () -> assertEquals("Analysis", response.title()),
                    () -> assertEquals("Exam prep", response.description()),
                    () -> assertEquals("uni", response.tag()),
                    () -> assertEquals(dueDate, response.dueDate()),
                    () -> assertEquals(StudyStatus.TO_DO, response.status())
            );
        }
        @Test
        void shouldThrowTaskNotFoundExceptionWhenTaskDoesNotExist() {
            Long id = -1L;
            when(studyTaskRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(TaskNotFoundException.class, () -> studyTaskService.getById(id));
            verify(studyTaskRepository).findById(id);
            verifyNoMoreInteractions(studyTaskRepository);
        }
    }

    @Nested class GetAllTasksTest {
        @Test
        void shouldGetAllTasks() {
            StudyTask first = new StudyTask(
                    "Analysis",
                    "Exam prep",
                    "uni",
                    LocalDate.of(2026, 9, 1),
                    StudyStatus.IN_PROGRESS
            );
            first.setId(1L);

            StudyTask second = new StudyTask(
                    "Programming",
                    "Learn Swagger",
                    "work",
                    LocalDate.of(2026, 8, 2),
                    StudyStatus.TO_DO
            );
            second.setId(2L);

            when(studyTaskRepository.findAll()).thenReturn(List.of(first, second));

            List<TaskResponse> responses = studyTaskService.getAll();
            verify(studyTaskRepository).findAll();

            assertEquals(2, responses.size());
            assertAll(
                    () -> assertEquals(1L, responses.get(0).id()),
                    () -> assertEquals("Analysis", responses.get(0).title()),
                    () -> assertEquals(StudyStatus.IN_PROGRESS, responses.get(0).status()),
                    () -> assertEquals(2L, responses.get(1).id()),
                    () -> assertEquals("Programming", responses.get(1).title()),
                    () -> assertEquals(StudyStatus.TO_DO, responses.get(1).status())
            );
        }

        @Test
        void shouldReturnEmptyListWhenNoTasksExist() {
            when(studyTaskRepository.findAll()).thenReturn(List.of());

            List<TaskResponse> responses = studyTaskService.getAll();
            verify(studyTaskRepository).findAll();

            assertTrue(responses.isEmpty());
        }
    }

    @Nested class UpdateTaskTest {
        @Test
        void shouldUpdateTaskWhenTaskExists() {
            Long id = 1L;
            LocalDate oldDueDate = LocalDate.of(2026, 8, 1);
            LocalDate newDueDate = LocalDate.of(2026, 9, 1);

            StudyTask existingTask = new StudyTask(
                    "Analysis",
                    "Exam prep",
                    "uni",
                    oldDueDate,
                    StudyStatus.TO_DO
            );
            existingTask.setId(id);

            UpdateTaskRequest request = new UpdateTaskRequest(
                    "Analysis",
                    "Exam prep",
                    "uni",
                    newDueDate,
                    StudyStatus.IN_PROGRESS
            );

            when(studyTaskRepository.findById(id)).thenReturn(Optional.of(existingTask));
            when(studyTaskRepository.save(any(StudyTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

            TaskResponse response = studyTaskService.update(id, request);

            verify(studyTaskRepository).findById(id);
            verify(studyTaskRepository).save(studyTaskCaptor.capture());

            StudyTask savedTask = studyTaskCaptor.getValue();

            assertAll("saved task",
                    () -> assertEquals(id, savedTask.getId()),
                    () -> assertEquals("Analysis", savedTask.getTitle()),
                    () -> assertEquals("Exam prep", savedTask.getDescription()),
                    () -> assertEquals("uni", savedTask.getTag()),
                    () -> assertEquals(newDueDate, savedTask.getDueDate()),
                    () -> assertEquals(StudyStatus.IN_PROGRESS, savedTask.getStatus())
            );

            assertAll("response",
                    () -> assertEquals(id, response.id()),
                    () -> assertEquals("Analysis", response.title()),
                    () -> assertEquals("Exam prep", response.description()),
                    () -> assertEquals("uni", response.tag()),
                    () -> assertEquals(newDueDate, response.dueDate()),
                    () -> assertEquals(StudyStatus.IN_PROGRESS, response.status())
            );
        }

        @Test
        void shouldThrowTaskNotFoundExceptionWhenTaskDoesNotExist() {
            Long id = -1L;

            UpdateTaskRequest request = new UpdateTaskRequest(
                    "Analysis",
                    "Exam prep",
                    "uni",
                    LocalDate.of(2026, 9, 1),
                    StudyStatus.IN_PROGRESS
            );

            when(studyTaskRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(TaskNotFoundException.class, () -> studyTaskService.update(id, request));

            verify(studyTaskRepository).findById(id);
            verify(studyTaskRepository, never()).save(any());
            verifyNoMoreInteractions(studyTaskRepository);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        void shouldNormalizeBlankDescriptionAndTagToNullWhenUpdatingTask(String blankValue) {
            Long id = 1L;
            LocalDate oldDueDate = LocalDate.of(2026, 8, 1);
            LocalDate newDueDate = LocalDate.of(2026, 9, 1);

            StudyTask existingTask = new StudyTask(
                    "Analysis",
                    blankValue,
                    blankValue,
                    oldDueDate,
                    StudyStatus.TO_DO
            );
            existingTask.setId(id);

            UpdateTaskRequest request = new UpdateTaskRequest(
                    "Analysis",
                    "",
                    "",
                    newDueDate,
                    StudyStatus.IN_PROGRESS
            );

            when(studyTaskRepository.findById(id)).thenReturn(Optional.of(existingTask));
            when(studyTaskRepository.save(any(StudyTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

            TaskResponse response = studyTaskService.update(id, request);

            verify(studyTaskRepository).findById(id);
            verify(studyTaskRepository).save(studyTaskCaptor.capture());

            StudyTask savedTask = studyTaskCaptor.getValue();

            assertAll("saved task",
                    () -> assertNull(savedTask.getDescription()),
                    () -> assertNull(savedTask.getTag()),
                    () -> assertEquals(newDueDate, savedTask.getDueDate()),
                    () -> assertEquals(StudyStatus.IN_PROGRESS, savedTask.getStatus())
            );

            assertAll("response",
                    () -> assertNull(response.description()),
                    () -> assertNull(response.tag()),
                    () -> assertEquals(newDueDate, response.dueDate()),
                    () -> assertEquals(StudyStatus.IN_PROGRESS, response.status())
            );
        }

        @Test
        void shouldTrimTextFieldsWhenUpdatingTask() {
            Long id = 1L;
            LocalDate oldDueDate = LocalDate.of(2026, 8, 1);
            LocalDate newDueDate = LocalDate.of(2026, 9, 1);

            StudyTask existingTask = new StudyTask(
                    "Analysis",
                    "Exam prep",
                    "uni",
                    oldDueDate,
                    StudyStatus.TO_DO
            );
            existingTask.setId(id);

            UpdateTaskRequest request = new UpdateTaskRequest(
                    "  Analysis  ",
                    "  Exam prep  ",
                    "  uni  ",
                    newDueDate,
                    StudyStatus.IN_PROGRESS
            );

            when(studyTaskRepository.findById(id)).thenReturn(Optional.of(existingTask));
            when(studyTaskRepository.save(any(StudyTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

            TaskResponse response = studyTaskService.update(id, request);

            verify(studyTaskRepository).findById(id);
            verify(studyTaskRepository).save(studyTaskCaptor.capture());

            StudyTask savedTask = studyTaskCaptor.getValue();

            assertAll("saved task",
                    () -> assertEquals(id, savedTask.getId()),
                    () -> assertEquals("Analysis", savedTask.getTitle()),
                    () -> assertEquals("Exam prep", savedTask.getDescription()),
                    () -> assertEquals("uni", savedTask.getTag()),
                    () -> assertEquals(newDueDate, savedTask.getDueDate()),
                    () -> assertEquals(StudyStatus.IN_PROGRESS, savedTask.getStatus())
            );

            assertAll("response",
                    () -> assertEquals(id, response.id()),
                    () -> assertEquals("Analysis", response.title()),
                    () -> assertEquals("Exam prep", response.description()),
                    () -> assertEquals("uni", response.tag()),
                    () -> assertEquals(newDueDate, response.dueDate()),
                    () -> assertEquals(StudyStatus.IN_PROGRESS, response.status())
            );
        }
    }

    @Nested class DeleteTaskTest {
        @Test
        void shouldDeleteTaskWhenTaskExists() {
            Long id = 1L;

            when(studyTaskRepository.existsById(id)).thenReturn(true);

            studyTaskService.deleteById(id);
            verify(studyTaskRepository).existsById(id);
            verify(studyTaskRepository).deleteById(id);
            verifyNoMoreInteractions(studyTaskRepository);
        }

        @Test
        void shouldThrowTaskNotFoundExceptionWhenTaskDoesNotExist() {
            Long id = -1L;
            when(studyTaskRepository.existsById(id)).thenReturn(false);

            assertThrows(TaskNotFoundException.class, () -> studyTaskService.deleteById(id));

            verify(studyTaskRepository).existsById(id);
            verify(studyTaskRepository, never()).deleteById(any());
            verifyNoMoreInteractions(studyTaskRepository);
        }
    }

    @Nested class SearchTasksTest {
        @Test
        void shouldReturnEmptyListWhenKeywordIsNull() {
            List<TaskResponse> result = studyTaskService.searchTasks(null);
            assertTrue(result.isEmpty());
            verifyNoInteractions(studyTaskRepository);
        }

        @Test
        void shouldReturnEmptyListWhenKeywordIsBlank() {
            List<TaskResponse> result = studyTaskService.searchTasks("   ");
            assertTrue(result.isEmpty());
            verifyNoInteractions(studyTaskRepository);
        }

        @Test
        void shouldTrimKeywordAndDelegateToRepository() {
            StudyTask task = new StudyTask("Analysis", null, null, LocalDate.of(2026, 9, 1), StudyStatus.TO_DO);
            task.setId(1L);
            when(studyTaskRepository.searchTasks("exam")).thenReturn(List.of(task));

            List<TaskResponse> result = studyTaskService.searchTasks("  exam  ");

            verify(studyTaskRepository).searchTasks("exam");
            assertEquals(1, result.size());
            assertEquals("Analysis", result.get(0).title());
        }
    }

}
