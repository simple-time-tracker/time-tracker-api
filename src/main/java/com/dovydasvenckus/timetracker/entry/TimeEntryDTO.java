package com.dovydasvenckus.timetracker.entry;

import com.dovydasvenckus.timetracker.project.ProjectDTO;

import java.time.LocalDateTime;

public class TimeEntryDTO {
    private Long id;

    private ProjectDTO project;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Long differenceInMinutes;

    TimeEntryDTO(){

    }

    TimeEntryDTO(TimeEntry timeEntry) {
        id = timeEntry.getId();
        project = new ProjectDTO(timeEntry.getProject());
        description = timeEntry.getDescription();
        startDate = timeEntry.getStartDate();
        endDate = timeEntry.getEndDate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Long getDifferenceInMinutes() {
        return differenceInMinutes;
    }

    public void setDifferenceInMinutes(Long differenceInMinutes) {
        this.differenceInMinutes = differenceInMinutes;
    }
}
