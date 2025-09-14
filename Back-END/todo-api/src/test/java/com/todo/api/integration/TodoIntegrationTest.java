package com.todo.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.api.model.Todo;
import com.todo.api.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Todo Integration Tests")
class TodoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
    }

    @Test
    @DisplayName("Should perform full CRUD operations on todos")
    void fullCrudOperations_ShouldWorkCorrectly() throws Exception {
        // Create a new todo
        Todo newTodo = new Todo();
        newTodo.setTitle("Integration Test Todo");
        newTodo.setDescription("Test Description");
        newTodo.setDueDate(LocalDateTime.now().plusDays(1));

        // Create todo via POST
        String todoJson = mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTodo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test Todo"))
                .andExpect(jsonPath("$.completed").value(false))
                .andReturn().getResponse().getContentAsString();

        Todo createdTodo = objectMapper.readValue(todoJson, Todo.class);
        Long todoId = createdTodo.getId();

        // Read todo via GET
        mockMvc.perform(get("/api/todos/" + todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoId))
                .andExpect(jsonPath("$.title").value("Integration Test Todo"));

        // Update todo via PUT
        createdTodo.setTitle("Updated Integration Test Todo");
        createdTodo.setCompleted(true);

        mockMvc.perform(put("/api/todos/" + todoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdTodo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Integration Test Todo"))
                .andExpect(jsonPath("$.completed").value(true));

        // Toggle status via PATCH
        mockMvc.perform(patch("/api/todos/" + todoId + "/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(false));

        // Delete todo via DELETE
        mockMvc.perform(delete("/api/todos/" + todoId))
                .andExpect(status().isNoContent());

        // Verify todo is deleted
        mockMvc.perform(get("/api/todos/" + todoId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should filter todos by completion status")
    void filterTodosByStatus_ShouldReturnCorrectResults() throws Exception {
        // Create completed todo
        Todo completedTodo = new Todo();
        completedTodo.setTitle("Completed Todo");
        completedTodo.setCompleted(true);
        todoRepository.save(completedTodo);

        // Create active todo
        Todo activeTodo = new Todo();
        activeTodo.setTitle("Active Todo");
        activeTodo.setCompleted(false);
        todoRepository.save(activeTodo);

        // Test filtering completed todos
        mockMvc.perform(get("/api/todos/status?completed=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Completed Todo"));

        // Test filtering active todos
        mockMvc.perform(get("/api/todos/status?completed=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Active Todo"));
    }

    @Test
    @DisplayName("Should search todos by title")
    void searchTodosByTitle_ShouldReturnMatchingResults() throws Exception {
        // Create todos with different titles
        Todo todo1 = new Todo();
        todo1.setTitle("Project Management");
        todoRepository.save(todo1);

        Todo todo2 = new Todo();
        todo2.setTitle("Project Documentation");
        todoRepository.save(todo2);

        Todo todo3 = new Todo();
        todo3.setTitle("Team Meeting");
        todoRepository.save(todo3);

        // Search for "Project"
        mockMvc.perform(get("/api/todos/search?title=Project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].title").value(org.hamcrest.Matchers.hasItems(
                    "Project Management", "Project Documentation")));
    }
}