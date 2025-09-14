package com.todo.api.service;

import com.todo.api.model.Todo;
import com.todo.api.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Todo Service Tests")
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

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
    @DisplayName("Should return all todos")
    void getAllTodos_ShouldReturnAllTodos() {
        // Given
        List<Todo> expectedTodos = Arrays.asList(sampleTodo);
        when(todoRepository.findAll()).thenReturn(expectedTodos);

        // When
        List<Todo> actualTodos = todoService.getAllTodos();

        // Then
        assertEquals(expectedTodos, actualTodos);
        verify(todoRepository).findAll();
    }

    @Test
    @DisplayName("Should return todo by ID when exists")
    void getTodoById_WhenTodoExists_ShouldReturnTodo() {
        // Given
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));

        // When
        Todo actualTodo = todoService.getTodoById(1L);

        // Then
        assertEquals(sampleTodo, actualTodo);
        verify(todoRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when todo not found")
    void getTodoById_WhenTodoNotExists_ShouldThrowException() {
        // Given
        when(todoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> todoService.getTodoById(1L)
        );
        
        assertEquals("Todo not found with id: 1", exception.getMessage());
        verify(todoRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return todos by completion status")
    void getTodosByStatus_ShouldReturnFilteredTodos() {
        // Given
        List<Todo> completedTodos = Arrays.asList(sampleTodo);
        when(todoRepository.findByCompleted(true)).thenReturn(completedTodos);

        // When
        List<Todo> actualTodos = todoService.getTodosByStatus(true);

        // Then
        assertEquals(completedTodos, actualTodos);
        verify(todoRepository).findByCompleted(true);
    }

    @Test
    @DisplayName("Should search todos by title")
    void searchTodos_ShouldReturnMatchingTodos() {
        // Given
        String searchTitle = "Test";
        List<Todo> matchingTodos = Arrays.asList(sampleTodo);
        when(todoRepository.findByTitleContainingIgnoreCase(searchTitle)).thenReturn(matchingTodos);

        // When
        List<Todo> actualTodos = todoService.searchTodos(searchTitle);

        // Then
        assertEquals(matchingTodos, actualTodos);
        verify(todoRepository).findByTitleContainingIgnoreCase(searchTitle);
    }

    @Test
    @DisplayName("Should create new todo")
    void createTodo_ShouldSaveAndReturnTodo() {
        // Given
        Todo newTodo = new Todo();
        newTodo.setTitle("New Todo");
        newTodo.setDescription("New Description");
        
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        // When
        Todo createdTodo = todoService.createTodo(newTodo);

        // Then
        assertEquals(sampleTodo, createdTodo);
        verify(todoRepository).save(newTodo);
    }

    @Test
    @DisplayName("Should update existing todo")
    void updateTodo_WhenTodoExists_ShouldUpdateAndReturnTodo() {
        // Given
        Todo updateDetails = new Todo();
        updateDetails.setTitle("Updated Title");
        updateDetails.setDescription("Updated Description");
        updateDetails.setCompleted(true);
        
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        // When
        Todo updatedTodo = todoService.updateTodo(1L, updateDetails);

        // Then
        assertEquals(sampleTodo, updatedTodo);
        verify(todoRepository).findById(1L);
        verify(todoRepository).save(sampleTodo);
        assertEquals("Updated Title", sampleTodo.getTitle());
        assertEquals("Updated Description", sampleTodo.getDescription());
        assertTrue(sampleTodo.isCompleted());
    }

    @Test
    @DisplayName("Should delete todo when exists")
    void deleteTodo_WhenTodoExists_ShouldDeleteTodo() {
        // Given
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));

        // When
        todoService.deleteTodo(1L);

        // Then
        verify(todoRepository).findById(1L);
        verify(todoRepository).delete(sampleTodo);
    }

    @Test
    @DisplayName("Should toggle todo completion status")
    void toggleTodoStatus_WhenTodoExists_ShouldToggleStatus() {
        // Given
        boolean originalStatus = sampleTodo.isCompleted();
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        // When
        Todo toggledTodo = todoService.toggleTodoStatus(1L);

        // Then
        assertEquals(sampleTodo, toggledTodo);
        assertEquals(!originalStatus, sampleTodo.isCompleted());
        verify(todoRepository).findById(1L);
        verify(todoRepository).save(sampleTodo);
    }
}