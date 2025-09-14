import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from 'react-query';
import { LocalizationProvider } from '@mui/lab';
import AdapterDateFns from '@mui/lab/AdapterDateFns';
import { TodoList } from '../components/TodoList';
import { TodoService } from '../services/TodoService';
import '@testing-library/jest-dom';

// Mock the TodoService
jest.mock('../services/TodoService');

const mockedTodoService = TodoService as jest.Mocked<typeof TodoService>;

const mockTodos = [
  {
    id: 1,
    title: 'Test Todo 1',
    description: 'Test Description 1',
    completed: false,
    createdAt: '2025-09-14T10:00:00Z',
    updatedAt: '2025-09-14T10:00:00Z'
  },
  {
    id: 2,
    title: 'Test Todo 2',
    description: 'Test Description 2',
    completed: true,
    createdAt: '2025-09-14T11:00:00Z',
    updatedAt: '2025-09-14T11:00:00Z'
  }
];

const TestWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });

  return (
    <QueryClientProvider client={queryClient}>
      <LocalizationProvider dateAdapter={AdapterDateFns}>
        {children}
      </LocalizationProvider>
    </QueryClientProvider>
  );
};

describe('TodoList Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders todo list with todos', async () => {
    mockedTodoService.getAllTodos.mockResolvedValue(mockTodos);

    render(
      <TestWrapper>
        <TodoList />
      </TestWrapper>
    );

    expect(screen.getByText('Todo List')).toBeInTheDocument();
    expect(screen.getByText('Add Todo')).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText('Test Todo 1')).toBeInTheDocument();
      expect(screen.getByText('Test Todo 2')).toBeInTheDocument();
    });
  });

  test('displays loading state initially', () => {
    mockedTodoService.getAllTodos.mockImplementation(() => new Promise(() => {}));

    render(
      <TestWrapper>
        <TodoList />
      </TestWrapper>
    );

    expect(screen.getByRole('progressbar')).toBeInTheDocument();
  });

  test('filters todos by completion status', async () => {
    mockedTodoService.getAllTodos.mockResolvedValue(mockTodos);

    render(
      <TestWrapper>
        <TodoList />
      </TestWrapper>
    );

    await waitFor(() => {
      expect(screen.getByText('Test Todo 1')).toBeInTheDocument();
    });

    // Filter by completed todos
    const filterSelect = screen.getByLabelText('Filter');
    fireEvent.mouseDown(filterSelect);
    
    const completedOption = screen.getByText('Completed');
    fireEvent.click(completedOption);

    // Should only show completed todo
    expect(screen.getByText('Test Todo 2')).toBeInTheDocument();
    expect(screen.queryByText('Test Todo 1')).not.toBeInTheDocument();
  });

  test('searches todos by title', async () => {
    mockedTodoService.getAllTodos.mockResolvedValue(mockTodos);

    render(
      <TestWrapper>
        <TodoList />
      </TestWrapper>
    );

    await waitFor(() => {
      expect(screen.getByText('Test Todo 1')).toBeInTheDocument();
    });

    // Search for "Todo 1"
    const searchInput = screen.getByLabelText('Search');
    fireEvent.change(searchInput, { target: { value: 'Todo 1' } });

    // Should only show matching todo
    expect(screen.getByText('Test Todo 1')).toBeInTheDocument();
    expect(screen.queryByText('Test Todo 2')).not.toBeInTheDocument();
  });

  test('opens add todo dialog when add button is clicked', async () => {
    mockedTodoService.getAllTodos.mockResolvedValue(mockTodos);

    render(
      <TestWrapper>
        <TodoList />
      </TestWrapper>
    );

    const addButton = screen.getByText('Add Todo');
    fireEvent.click(addButton);

    await waitFor(() => {
      expect(screen.getByText('New Todo')).toBeInTheDocument();
    });
  });

  test('displays empty state when no todos match filter', async () => {
    mockedTodoService.getAllTodos.mockResolvedValue([]);

    render(
      <TestWrapper>
        <TodoList />
      </TestWrapper>
    );

    await waitFor(() => {
      expect(screen.getByText('No todos found')).toBeInTheDocument();
    });
  });
});