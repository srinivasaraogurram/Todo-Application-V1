import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { TodoItem } from '../components/TodoItem';
import { Todo } from '../types/Todo';
import '@testing-library/jest-dom';

const mockTodo: Todo = {
  id: 1,
  title: 'Test Todo',
  description: 'Test Description',
  completed: false,
  dueDate: '2025-09-21T15:00:00Z',
  createdAt: '2025-09-14T10:00:00Z',
  updatedAt: '2025-09-14T10:00:00Z'
};

const mockCallbacks = {
  onToggle: jest.fn(),
  onEdit: jest.fn(),
  onDelete: jest.fn()
};

describe('TodoItem Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders todo item correctly', () => {
    render(<TodoItem todo={mockTodo} {...mockCallbacks} />);

    expect(screen.getByText('Test Todo')).toBeInTheDocument();
    expect(screen.getByText('Test Description')).toBeInTheDocument();
    expect(screen.getByText(/Due: September 21st, 2025/)).toBeInTheDocument();
    
    const checkbox = screen.getByRole('checkbox');
    expect(checkbox).not.toBeChecked();
  });

  test('renders completed todo with strike-through text', () => {
    const completedTodo = { ...mockTodo, completed: true };
    
    render(<TodoItem todo={completedTodo} {...mockCallbacks} />);

    const title = screen.getByText('Test Todo');
    expect(title).toHaveStyle('text-decoration: line-through');
  });

  test('calls onToggle when checkbox is clicked', () => {
    render(<TodoItem todo={mockTodo} {...mockCallbacks} />);

    const checkbox = screen.getByRole('checkbox');
    fireEvent.click(checkbox);

    expect(mockCallbacks.onToggle).toHaveBeenCalledWith(1);
  });

  test('calls onEdit when edit button is clicked', () => {
    render(<TodoItem todo={mockTodo} {...mockCallbacks} />);

    const editButton = screen.getByTestId('EditIcon').closest('button');
    expect(editButton).toBeInTheDocument();
    
    fireEvent.click(editButton!);

    expect(mockCallbacks.onEdit).toHaveBeenCalledWith(mockTodo);
  });

  test('calls onDelete when delete button is clicked', () => {
    render(<TodoItem todo={mockTodo} {...mockCallbacks} />);

    const deleteButton = screen.getByTestId('DeleteIcon').closest('button');
    expect(deleteButton).toBeInTheDocument();
    
    fireEvent.click(deleteButton!);

    expect(mockCallbacks.onDelete).toHaveBeenCalledWith(1);
  });

  test('renders todo without description', () => {
    const todoWithoutDescription = { ...mockTodo, description: undefined };
    
    render(<TodoItem todo={todoWithoutDescription} {...mockCallbacks} />);

    expect(screen.getByText('Test Todo')).toBeInTheDocument();
    expect(screen.queryByText('Test Description')).not.toBeInTheDocument();
  });

  test('renders todo without due date', () => {
    const todoWithoutDueDate = { ...mockTodo, dueDate: undefined };
    
    render(<TodoItem todo={todoWithoutDueDate} {...mockCallbacks} />);

    expect(screen.getByText('Test Todo')).toBeInTheDocument();
    expect(screen.queryByText(/Due:/)).not.toBeInTheDocument();
  });

  test('does not call onToggle when todo has no id', () => {
    const todoWithoutId = { ...mockTodo, id: undefined };
    
    render(<TodoItem todo={todoWithoutId} {...mockCallbacks} />);

    const checkbox = screen.getByRole('checkbox');
    fireEvent.click(checkbox);

    expect(mockCallbacks.onToggle).not.toHaveBeenCalled();
  });

  test('does not call onDelete when todo has no id', () => {
    const todoWithoutId = { ...mockTodo, id: undefined };
    
    render(<TodoItem todo={todoWithoutId} {...mockCallbacks} />);

    const deleteButton = screen.getByTestId('DeleteIcon').closest('button');
    fireEvent.click(deleteButton!);

    expect(mockCallbacks.onDelete).not.toHaveBeenCalled();
  });
});