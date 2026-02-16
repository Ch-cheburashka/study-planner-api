package com.masha.studyplannerapi.repository;

import com.masha.studyplannerapi.domain.entity.StudyTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {}
