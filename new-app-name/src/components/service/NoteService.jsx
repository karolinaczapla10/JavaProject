import axios from 'axios';

const API_URL = 'http://localhost:8080/notes';  // Adjust according to your backend URL

const NoteService = {
    createNote: (content, token) => {
        return axios.post(`${API_URL}/create`, content, {
            headers: {
                'Authorization': `Bearer ${token}`,
            }
        });
    },
    updateNote: (noteId, newContent, token) => {
        return axios.put(`${API_URL}/update/${noteId}`, newContent, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'  // Dodaj Content-Type
            }
        });
    },
    
    deleteNote: (noteId, token) => {
        return axios.delete(`${API_URL}/delete/${noteId}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
            }
        });
    },
    getAllNotes: (token) => {
        return axios.get(`${API_URL}/all`, {
            headers: {
                'Authorization': `Bearer ${token}`,
            }
        });
    },
};

export default NoteService;
