package com.todo.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "todos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Todo entity representing a task in the todo list")
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the todo", example = "1")
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    @Schema(description = "Title of the todo", example = "Complete project documentation", required = true)
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Detailed description of the todo", example = "Write comprehensive documentation for the Todo application")
    private String description;

    @Schema(description = "Completion status of the todo", example = "false")
    private boolean completed;

    @Schema(description = "Due date of the todo", example = "2025-09-21T15:00:00Z")
    private LocalDateTime dueDate;

    @Schema(description = "Creation timestamp of the todo", example = "2025-09-14T10:30:00Z")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp of the todo", example = "2025-09-14T10:30:00Z")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}