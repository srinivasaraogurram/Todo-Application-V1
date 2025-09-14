import axios from 'axios';
import { TodoService } from '../services/TodoService';
import { Todo } from '../types/Todo';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

const mockTodo: Todo = {
  id: 1,
  title: 'Test Todo',
  description: 'Test Description',
  completed: false,
  dueDate: '2025-09-21T15:00:00Z',
  createdAt: '2025-09-14T10:00:00Z',
  updatedAt: '2025-09-14T10:00:00Z'
};

describe('TodoService', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('getAllTodos', () => {
    test('should fetch all todos successfully', async () => {
      const mockTodos = [mockTodo];
      mockedAxios.get.mockResolvedValue({ data: mockTodos });

      const result = await TodoService.getAllTodos();

      expect(mockedAxios.get).toHaveBeenCalledWith('http://localhost:8080/api/todos');
      expect(result).toEqual(mockTodos);
    });

    test('should handle error when fetching todos fails', async () => {
      const errorMessage = 'Network Error';
      mockedAxios.get.mockRejectedValue(new Error(errorMessage));

      await expect(TodoService.getAllTodos()).rejects.toThrow(errorMessage);
    });
  });

  describe('getTodoById', () => {
    test('should fetch todo by id successfully', async () => {
      mockedAxios.get.mockResolvedValue({ data: mockTodo });

      const result = await TodoService.getTodoById(1);

      expect(mockedAxios.get).toHaveBeenCalledWith('http://localhost:8080/api/todos/1');
      expect(result).toEqual(mockTodo);
    });
  });

  describe('getTodosByStatus', () => {
    test('should fetch todos by completion status', async () => {
      const completedTodos = [{ ...mockTodo, completed: true }];
      mockedAxios.get.mockResolvedValue({ data: completedTodos });

      const result = await TodoService.getTodosByStatus(true);

      expect(mockedAxios.get).toHaveBeenCalledWith('http://localhost:8080/api/todos/status?completed=true');
      expect(result).toEqual(completedTodos);
    });
  });

  describe('searchTodos', () => {
    test('should search todos by title', async () => {
      const searchResults = [mockTodo];
      mockedAxios.get.mockResolvedValue({ data: searchResults });

      const result = await TodoService.searchTodos('Test');

      expect(mockedAxios.get).toHaveBeenCalledWith('http://localhost:8080/api/todos/search?title=Test');
      expect(result).toEqual(searchResults);
    });

    test('should encode search query properly', async () => {
      const searchResults = [mockTodo];
      mockedAxios.get.mockResolvedValue({ data: searchResults });

      await TodoService.searchTodos('Test & Query');

      expect(mockedAxios.get).toHaveBeenCalledWith('http://localhost:8080/api/todos/search?title=Test%20%26%20Query');
    });
  });

  describe('createTodo', () => {
    test('should create new todo successfully', async () => {
      const newTodo = { title: 'New Todo', description: 'New Description', completed: false };
      mockedAxios.post.mockResolvedValue({ data: mockTodo });

      const result = await TodoService.createTodo(newTodo);

      expect(mockedAxios.post).toHaveBeenCalledWith('http://localhost:8080/api/todos', newTodo);
      expect(result).toEqual(mockTodo);
    });
  });

  describe('updateTodo', () => {
    test('should update todo successfully', async () => {
      const updatedTodo = { ...mockTodo, title: 'Updated Todo' };
      mockedAxios.put.mockResolvedValue({ data: updatedTodo });

      const result = await TodoService.updateTodo(1, updatedTodo);

      expect(mockedAxios.put).toHaveBeenCalledWith('http://localhost:8080/api/todos/1', updatedTodo);
      expect(result).toEqual(updatedTodo);
    });
  });

  describe('deleteTodo', () => {
    test('should delete todo successfully', async () => {
      mockedAxios.delete.mockResolvedValue({});

      await TodoService.deleteTodo(1);

      expect(mockedAxios.delete).toHaveBeenCalledWith('http://localhost:8080/api/todos/1');
    });
  });

  describe('toggleTodoStatus', () => {
    test('should toggle todo status successfully', async () => {
      const toggledTodo = { ...mockTodo, completed: true };
      mockedAxios.patch.mockResolvedValue({ data: toggledTodo });

      const result = await TodoService.toggleTodoStatus(1);

      expect(mockedAxios.patch).toHaveBeenCalledWith('http://localhost:8080/api/todos/1/toggle');
      expect(result).toEqual(toggledTodo);
    });
  });
});