package com.masha.studyplannerapi.domain.entity;

import com.masha.studyplannerapi.domain.enums.StudyStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "study_planner")
public class StudyTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    private String description;
    private String tag;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyStatus status;

    public StudyTask() {
    }

    public StudyTask(String title, String description, String tag, LocalDate dueDate, StudyStatus status) {
        this.title = title;
        this.description = description;
        this.tag = tag;
        this.dueDate = dueDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTag() {
        return tag;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public StudyStatus getStatus() {
        return status;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public void setStatus(StudyStatus status) {
        this.status = status;
    }
}
