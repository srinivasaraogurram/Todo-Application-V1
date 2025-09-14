import axios from 'axios';
import { Todo } from '../types/Todo';

const API_BASE_URL = 'http://localhost:8080/api/todos';

export const TodoService = {
    getAllTodos: async (): Promise<Todo[]> => {
        const response = await axios.get(API_BASE_URL);
        return response.data;
    },

    getTodoById: async (id: number): Promise<Todo> => {
        const response = await axios.get(`${API_BASE_URL}/${id}`);
        return response.data;
    },

    getTodosByStatus: async (completed: boolean): Promise<Todo[]> => {
        const response = await axios.get(`${API_BASE_URL}/status?completed=${completed}`);
        return response.data;
    },

    searchTodos: async (title: string): Promise<Todo[]> => {
        const response = await axios.get(`${API_BASE_URL}/search?title=${encodeURIComponent(title)}`);
        return response.data;
    },

    createTodo: async (todo: Omit<Todo, 'id'>): Promise<Todo> => {
        const response = await axios.post(API_BASE_URL, todo);
        return response.data;
    },

    updateTodo: async (id: number, todo: Todo): Promise<Todo> => {
        const response = await axios.put(`${API_BASE_URL}/${id}`, todo);
        return response.data;
    },

    deleteTodo: async (id: number): Promise<void> => {
        await axios.delete(`${API_BASE_URL}/${id}`);
    },

    toggleTodoStatus: async (id: number): Promise<Todo> => {
        const response = await axios.patch(`${API_BASE_URL}/${id}/toggle`);
        return response.data;
    }
};