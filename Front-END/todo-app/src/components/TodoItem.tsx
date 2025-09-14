import React from 'react';
import { Todo } from '../types/Todo';
import { Card, CardContent, Typography, IconButton, Box, Checkbox } from '@mui/material';
import { Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';
import { format } from 'date-fns';

interface TodoItemProps {
    todo: Todo;
    onToggle: (id: number) => void;
    onEdit: (todo: Todo) => void;
    onDelete: (id: number) => void;
}

export const TodoItem: React.FC<TodoItemProps> = ({ todo, onToggle, onEdit, onDelete }) => {
    return (
        <Card sx={{ mb: 2, backgroundColor: todo.completed ? '#f5f5f5' : 'white' }}>
            <CardContent>
                <Box display="flex" alignItems="center" justifyContent="space-between">
                    <Box display="flex" alignItems="center" flex={1}>
                        <Checkbox
                            checked={todo.completed}
                            onChange={() => todo.id && onToggle(todo.id)}
                        />
                        <Box>
                            <Typography
                                variant="h6"
                                sx={{
                                    textDecoration: todo.completed ? 'line-through' : 'none',
                                    color: todo.completed ? 'text.secondary' : 'text.primary'
                                }}
                            >
                                {todo.title}
                            </Typography>
                            {todo.description && (
                                <Typography variant="body2" color="text.secondary">
                                    {todo.description}
                                </Typography>
                            )}
                            {todo.dueDate && (
                                <Typography variant="caption" color="text.secondary">
                                    Due: {format(new Date(todo.dueDate), 'PPP')}
                                </Typography>
                            )}
                        </Box>
                    </Box>
                    <Box>
                        <IconButton onClick={() => onEdit(todo)} size="small">
                            <EditIcon />
                        </IconButton>
                        <IconButton onClick={() => todo.id && onDelete(todo.id)} size="small" color="error">
                            <DeleteIcon />
                        </IconButton>
                    </Box>
                </Box>
            </CardContent>
        </Card>
    );
};