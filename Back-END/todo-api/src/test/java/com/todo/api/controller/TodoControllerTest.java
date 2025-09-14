package com.todo.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.api.model.Todo;
import com.todo.api.service.TodoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
@DisplayName("Todo Controller Tests")
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Todo sampleTodo;

    @BeforeEach
    void setUp() {
        sampleTodo = new Todo();
        sampleTodo.setId(1L);
        sampleTodo.setTitle("Test Todo");
        sampleTodo.setDescription("Test Description");
        sampleTodo.setCompleted(false);
        sampleTodo.setCreatedAt(LocalDateTime.now());
        sampleTodo.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /api/todos should return all todos")
    void getAllTodos_ShouldReturnListOfTodos() throws Exception {
        // Given
        List<Todo> todos = Arrays.asList(sampleTodo);
        when(todoService.getAllTodos()).thenReturn(todos);

        // When & Then
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Todo"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].completed").value(false));

        verify(todoService).getAllTodos();
    }

    @Test
    @DisplayName("GET /api/todos/{id} should return todo by ID")
    void getTodoById_WhenTodoExists_ShouldReturnTodo() throws Exception {
        // Given
        when(todoService.getTodoById(1L)).thenReturn(sampleTodo);

        // When & Then
        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Todo"));

        verify(todoService).getTodoById(1L);
    }

    @Test
    @DisplayName("GET /api/todos/{id} should return 404 when todo not found")
    void getTodoById_WhenTodoNotExists_ShouldReturn404() throws Exception {
        // Given
        when(todoService.getTodoById(1L)).thenThrow(new EntityNotFoundException("Todo not found with id: 1"));

        // When & Then
        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isNotFound());

        verify(todoService).getTodoById(1L);
    }

    @Test
    @DisplayName("GET /api/todos/status should filter todos by completion status")
    void getTodosByStatus_ShouldReturnFilteredTodos() throws Exception {
        // Given
        List<Todo> completedTodos = Arrays.asList(sampleTodo);
        when(todoService.getTodosByStatus(true)).thenReturn(completedTodos);

        // When & Then
        mockMvc.perform(get("/api/todos/status")
                .param("completed", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(todoService).getTodosByStatus(true);
    }

    @Test
    @DisplayName("GET /api/todos/search should search todos by title")
    void searchTodos_ShouldReturnMatchingTodos() throws Exception {
        // Given
        List<Todo> matchingTodos = Arrays.asList(sampleTodo);
        when(todoService.searchTodos("Test")).thenReturn(matchingTodos);

        // When & Then
        mockMvc.perform(get("/api/todos/search")
                .param("title", "Test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(todoService).searchTodos("Test");
    }

    @Test
    @DisplayName("POST /api/todos should create new todo")
    void createTodo_WithValidData_ShouldCreateTodo() throws Exception {
        // Given
        Todo newTodo = new Todo();
        newTodo.setTitle("New Todo");
        newTodo.setDescription("New Description");
        
        when(todoService.createTodo(any(Todo.class))).thenReturn(sampleTodo);

        // When & Then
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTodo)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));

        verify(todoService).createTodo(any(Todo.class));
    }

    @Test
    @DisplayName("POST /api/todos should return 400 for invalid data")
    void createTodo_WithInvalidData_ShouldReturn400() throws Exception {
        // Given
        Todo invalidTodo = new Todo();
        // Missing required title

        // When & Then
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTodo)))
                .andExpect(status().isBadRequest());

        verify(todoService, never()).createTodo(any(Todo.class));
    }

    @Test
    @DisplayName("PUT /api/todos/{id} should update existing todo")
    void updateTodo_WithValidData_ShouldUpdateTodo() throws Exception {
        // Given
        Todo updateData = new Todo();
        updateData.setTitle("Updated Todo");
        updateData.setDescription("Updated Description");
        updateData.setCompleted(true);
        
        when(todoService.updateTodo(eq(1L), any(Todo.class))).thenReturn(sampleTodo);

        // When & Then
        mockMvc.perform(put("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));

        verify(todoService).updateTodo(eq(1L), any(Todo.class));
    }

    @Test
    @DisplayName("DELETE /api/todos/{id} should delete todo")
    void deleteTodo_WhenTodoExists_ShouldDeleteTodo() throws Exception {
        // Given
        doNothing().when(todoService).deleteTodo(1L);

        // When & Then
        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());

        verify(todoService).deleteTodo(1L);
    }

    @Test
    @DisplayName("PATCH /api/todos/{id}/toggle should toggle todo status")
    void toggleTodoStatus_WhenTodoExists_ShouldToggleStatus() throws Exception {
        // Given
        Todo toggledTodo = new Todo();
        toggledTodo.setId(1L);
        toggledTodo.setTitle("Test Todo");
        toggledTodo.setCompleted(true);
        
        when(todoService.toggleTodoStatus(1L)).thenReturn(toggledTodo);

        // When & Then
        mockMvc.perform(patch("/api/todos/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.completed").value(true));

        verify(todoService).toggleTodoStatus(1L);
    }
}