package com.todo.api.controller;

import com.todo.api.model.Todo;
import com.todo.api.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Tag(name = "Todo", description = "Todo management APIs")
@CrossOrigin(origins = "http://localhost:3000")
public class TodoController {
    private final TodoService todoService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Get all todos",
        description = "Retrieves a list of all todos in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved todos",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Todo.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<Todo>> getAllTodos() {
        return ResponseEntity.ok(todoService.getAllTodos());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Get todo by ID",
        description = "Retrieves a specific todo by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved todo",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Todo.class))),
        @ApiResponse(responseCode = "404", description = "Todo not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<Todo> getTodoById(
            @Parameter(description = "ID of the todo to retrieve", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Get todos by status",
        description = "Retrieves todos filtered by completion status"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered todos",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Todo.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<Todo>> getTodosByStatus(
            @Parameter(description = "Completion status to filter by", required = true) 
            @RequestParam boolean completed) {
        return ResponseEntity.ok(todoService.getTodosByStatus(completed));
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Search todos",
        description = "Searches todos by title (case-insensitive)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matching todos",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Todo.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<Todo>> searchTodos(
            @Parameter(description = "Title to search for", required = true) @RequestParam String title) {
        return ResponseEntity.ok(todoService.searchTodos(title));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Create todo",
        description = "Creates a new todo item"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Todo successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Todo.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<Todo> createTodo(
            @Parameter(description = "Todo object to be created", required = true, 
                      schema = @Schema(implementation = Todo.class))
            @Valid @RequestBody Todo todo) {
        return new ResponseEntity<>(todoService.createTodo(todo), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Update todo",
        description = "Updates an existing todo"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Todo successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Todo.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
        @ApiResponse(responseCode = "404", description = "Todo not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<Todo> updateTodo(
            @Parameter(description = "ID of the todo to update", required = true) @PathVariable Long id,
            @Parameter(description = "Updated todo object", required = true, 
                      schema = @Schema(implementation = Todo.class))
            @Valid @RequestBody Todo todoDetails) {
        return ResponseEntity.ok(todoService.updateTodo(id, todoDetails));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete todo",
        description = "Deletes a todo permanently"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Todo successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Todo not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<Void> deleteTodo(
            @Parameter(description = "ID of the todo to delete", required = true) @PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/toggle", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Toggle todo status",
        description = "Toggles the completion status of a todo (completed ↔ not completed)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Todo status successfully toggled",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Todo.class))),
        @ApiResponse(responseCode = "404", description = "Todo not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<Todo> toggleTodoStatus(
            @Parameter(description = "ID of the todo to toggle", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(todoService.toggleTodoStatus(id));
    }
}