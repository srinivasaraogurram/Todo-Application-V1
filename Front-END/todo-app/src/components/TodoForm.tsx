import React from 'react';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import { Dialog, DialogTitle, DialogContent, DialogActions, TextField, Button } from '@mui/material';
import { DateTimePicker } from '@mui/lab';
import { Todo } from '../types/Todo';

interface TodoFormProps {
    open: boolean;
    onClose: () => void;
    onSubmit: (values: Omit<Todo, 'id'>) => void;
    initialValues?: Todo;
}

const validationSchema = Yup.object({
    title: Yup.string()
        .required('Title is required')
        .max(100, 'Title cannot exceed 100 characters'),
    description: Yup.string()
        .max(500, 'Description cannot exceed 500 characters'),
    dueDate: Yup.date()
        .min(new Date(), 'Due date cannot be in the past')
});

export const TodoForm: React.FC<TodoFormProps> = ({ open, onClose, onSubmit, initialValues }) => {
    const formik = useFormik({
        initialValues: {
            title: initialValues?.title || '',
            description: initialValues?.description || '',
            completed: initialValues?.completed || false,
            dueDate: initialValues?.dueDate ? new Date(initialValues.dueDate) : null
        },
        validationSchema,
        onSubmit: (values) => {
            onSubmit({
                ...values,
                dueDate: values.dueDate?.toISOString()
            });
            onClose();
        },
    });

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <form onSubmit={formik.handleSubmit}>
                <DialogTitle>{initialValues ? 'Edit Todo' : 'New Todo'}</DialogTitle>
                <DialogContent>
                    <TextField
                        fullWidth
                        margin="normal"
                        id="title"
                        name="title"
                        label="Title"
                        value={formik.values.title}
                        onChange={formik.handleChange}
                        error={formik.touched.title && Boolean(formik.errors.title)}
                        helperText={formik.touched.title && formik.errors.title}
                    />
                    <TextField
                        fullWidth
                        margin="normal"
                        id="description"
                        name="description"
                        label="Description"
                        multiline
                        rows={4}
                        value={formik.values.description}
                        onChange={formik.handleChange}
                        error={formik.touched.description && Boolean(formik.errors.description)}
                        helperText={formik.touched.description && formik.errors.description}
                    />
                    <DateTimePicker
                        label="Due Date"
                        value={formik.values.dueDate}
                        onChange={(date) => formik.setFieldValue('dueDate', date)}
                        renderInput={(params) => (
                            <TextField
                                {...params}
                                fullWidth
                                margin="normal"
                                error={formik.touched.dueDate && Boolean(formik.errors.dueDate)}
                                helperText={formik.touched.dueDate && formik.errors.dueDate}
                            />
                        )}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={onClose}>Cancel</Button>
                    <Button type="submit" variant="contained" color="primary">
                        {initialValues ? 'Save' : 'Create'}
                    </Button>
                </DialogActions>
            </form>
        </Dialog>
    );
};