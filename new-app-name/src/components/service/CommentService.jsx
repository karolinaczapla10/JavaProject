// src/service/CommentService.js
import axios from 'axios';

const API_URL = 'http://localhost:8080/comments';

const addComment = async (noteId, content) => {
    const token = localStorage.getItem('token');
    const response = await axios.post(`${API_URL}/add/${noteId}`, content, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    return response.data;
};

const getComments = async (noteId) => {
    const response = await axios.get(`${API_URL}/all/${noteId}`);
    return response.data;
};

export default {
    addComment,
    getComments
};
