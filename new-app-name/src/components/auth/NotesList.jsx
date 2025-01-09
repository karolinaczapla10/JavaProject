import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import NoteService from '../service/NoteService';

function NotesList() {
    const [notes, setNotes] = useState([]);
    const [error, setError] = useState('');
    const [newNoteContent, setNewNoteContent] = useState('');
    const [editingNoteId, setEditingNoteId] = useState(null);
    const [editingNoteContent, setEditingNoteContent] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchNotes = async () => {
            try {
                const token = localStorage.getItem('token');
                if (!token) {
                    navigate('/login');
                    return;
                }
                const response = await NoteService.getAllNotes(token);
                setNotes(response.data);
            } catch (error) {
                console.error('Error fetching notes:', error);
                setError('Error fetching notes');
            }
        };

        fetchNotes();
    }, [navigate]);

    const handleCreateNote = async () => {
        if (!newNoteContent.trim()) {
            setError('Note content cannot be empty.');
            return;
        }
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                navigate('/login');
                return;
            }
            const contentWithoutQuotes = newNoteContent.replace(/"/g, ''); // Remove quotes
            const response = await NoteService.createNote(contentWithoutQuotes, token);
            setNotes([response.data, ...notes]);
            setNewNoteContent('');
        } catch (error) {
            console.error('Error creating note:', error);
            setError('Error creating note');
        }
    };

    const handleDeleteNote = async (noteId) => {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                navigate('/login');
                return;
            }
            await NoteService.deleteNote(noteId, token);
            setNotes(notes.filter(note => note.id !== noteId));
        } catch (error) {
            console.error('Error deleting note:', error);
            setError('Error deleting note');
        }
    };

    const startEditing = (noteId, currentContent) => {
        setEditingNoteId(noteId);
        setEditingNoteContent(currentContent);
    };

    const handleUpdateNote = async (noteId) => {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                navigate('/login');
                return;
            }
            const contentWithoutQuotes = editingNoteContent.replace(/"/g, ''); // Remove quotes
            const response = await NoteService.updateNote(noteId, contentWithoutQuotes, token);
            setNotes(notes.map(note => note.id === noteId ? response.data : note));
            setEditingNoteId(null);
            setEditingNoteContent('');
        } catch (error) {
            console.error('Error updating note:', error);
            setError('Error updating note');
        }
    };

    return (
        <div className="notes-list-container">
            <h2>Notes</h2>
            {error && <p className="error-message">{error}</p>}

            <div className="new-note-container">
                <textarea
                    value={newNoteContent}
                    onChange={(e) => setNewNoteContent(e.target.value)}
                    placeholder="Write a new note"
                />
                <button onClick={handleCreateNote}>Create Note</button>
            </div>
            
            <div className="notes-container">
                {notes.map(note => (
                <div key={note.id} className="note-item">
                    <div className="note-content">
                    {editingNoteId === note.id ? (
                        <textarea
                        value={editingNoteContent}
                        onChange={(e) => setEditingNoteContent(e.target.value)}
                        />
                    ) : (
                        <p>{note.content.replace(/"/g, '')}</p>
                    )}
                    </div>
                    <div className="note-actions">
                    {editingNoteId === note.id ? (
                        <div>
                        <button onClick={() => handleUpdateNote(note.id)}>Save</button>
                        <button onClick={() => setEditingNoteId(null)}>Cancel</button>
                        </div>
                    ) : (
                        <div>
                        <button onClick={() => startEditing(note.id, note.content)}>Edit</button>
                        <button onClick={() => handleDeleteNote(note.id)}>Delete</button>
                        </div>
                    )}
                    </div>
                </div>
                ))}
            </div>
        </div>
    );
}

export default NotesList;
