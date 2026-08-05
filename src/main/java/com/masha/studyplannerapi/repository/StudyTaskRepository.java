package com.masha.studyplannerapi.repository;

import com.masha.studyplannerapi.domain.entity.StudyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {
    @Query("SELECT t from StudyTask t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<StudyTask> searchTasks(String keyword);
}
