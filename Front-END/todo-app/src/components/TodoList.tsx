import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { TodoService } from '../services/TodoService';
import { Todo } from '../types/Todo';
import { TodoItem } from './TodoItem';
import { TodoForm } from './TodoForm';
import {
    Container,
    Typography,
    Button,
    Box,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    CircularProgress
} from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';

export const TodoList: React.FC = () => {
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [selectedTodo, setSelectedTodo] = useState<Todo | null>(null);
    const [filter, setFilter] = useState<'all' | 'active' | 'completed'>('all');
    const [searchQuery, setSearchQuery] = useState('');

    const queryClient = useQueryClient();

    const { data: todos, isLoading } = useQuery(['todos'], TodoService.getAllTodos);

    const createMutation = useMutation(TodoService.createTodo, {
        onSuccess: () => queryClient.invalidateQueries(['todos'])
    });

    const updateMutation = useMutation(
        ({ id, todo }: { id: number; todo: Todo }) => TodoService.updateTodo(id, todo),
        {
            onSuccess: () => queryClient.invalidateQueries(['todos'])
        }
    );

    const deleteMutation = useMutation(TodoService.deleteTodo, {
        onSuccess: () => queryClient.invalidateQueries(['todos'])
    });

    const toggleMutation = useMutation(TodoService.toggleTodoStatus, {
        onSuccess: () => queryClient.invalidateQueries(['todos'])
    });

    const handleSubmit = (todo: Omit<Todo, 'id'>) => {
        if (selectedTodo?.id) {
            updateMutation.mutate({ id: selectedTodo.id, todo: { ...todo, id: selectedTodo.id } });
        } else {
            createMutation.mutate(todo);
        }
        setIsFormOpen(false);
        setSelectedTodo(null);
    };

    const handleEdit = (todo: Todo) => {
        setSelectedTodo(todo);
        setIsFormOpen(true);
    };

    const filteredTodos = todos?.filter(todo => {
        const matchesFilter =
            filter === 'all' ||
            (filter === 'active' && !todo.completed) ||
            (filter === 'completed' && todo.completed);
        
        const matchesSearch =
            searchQuery === '' ||
            todo.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
            todo.description?.toLowerCase().includes(searchQuery.toLowerCase());

        return matchesFilter && matchesSearch;
    });

    if (isLoading) {
        return (
            <Box display="flex" justifyContent="center" m={4}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Container maxWidth="md">
            <Box py={4}>
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
                    <Typography variant="h4" component="h1">
                        Todo List
                    </Typography>
                    <Button
                        variant="contained"
                        color="primary"
                        startIcon={<AddIcon />}
                        onClick={() => {
                            setSelectedTodo(null);
                            setIsFormOpen(true);
                        }}
                    >
                        Add Todo
                    </Button>
                </Box>

                <Box display="flex" gap={2} mb={4}>
                    <TextField
                        label="Search"
                        variant="outlined"
                        size="small"
                        fullWidth
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                    />
                    <FormControl size="small" style={{ minWidth: 120 }}>
                        <InputLabel>Filter</InputLabel>
                        <Select
                            value={filter}
                            label="Filter"
                            onChange={(e) => setFilter(e.target.value as typeof filter)}
                        >
                            <MenuItem value="all">All</MenuItem>
                            <MenuItem value="active">Active</MenuItem>
                            <MenuItem value="completed">Completed</MenuItem>
                        </Select>
                    </FormControl>
                </Box>

                {filteredTodos?.map(todo => (
                    <TodoItem
                        key={todo.id}
                        todo={todo}
                        onToggle={() => todo.id && toggleMutation.mutate(todo.id)}
                        onEdit={handleEdit}
                        onDelete={(id) => deleteMutation.mutate(id)}
                    />
                ))}

                {filteredTodos?.length === 0 && (
                    <Typography color="text.secondary" align="center">
                        No todos found
                    </Typography>
                )}
            </Box>

            <TodoForm
                open={isFormOpen}
                onClose={() => {
                    setIsFormOpen(false);
                    setSelectedTodo(null);
                }}
                onSubmit={handleSubmit}
                initialValues={selectedTodo || undefined}
            />
        </Container>
    );
};