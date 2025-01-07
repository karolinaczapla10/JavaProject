import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import NoteService from '../service/NoteService'; // Import the NoteService

function NotesList() {
    const [notes, setNotes] = useState([]);
    const [error, setError] = useState('');
    const [newNoteContent, setNewNoteContent] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchNotes = async () => {
            try {
                const token = localStorage.getItem('token');
                console.log('Token:', token);  // Log the token

                // If no token, redirect to login
                if (!token) {
                    navigate('/login');
                    return;
                }

                // Use NoteService to fetch notes
                const response = await NoteService.getAllNotes(token);

                // Extract notes from the response
                setNotes(response.data);
            } catch (error) {
                console.error('Error fetching notes:', error);
                setError('Error fetching notes');
            }
        };

        fetchNotes();
    }, [navigate]);

    // Handle creating a new note
    const handleCreateNote = async () => {
        if (!newNoteContent.trim()) { // Walidacja pustego pola
            setError('Note content cannot be empty.');
            return;
        }
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                navigate('/login');
                return;
            }

            const response = await NoteService.createNote(newNoteContent, token);
            setNotes([response.data, ...notes]);  // Prepend the new note
            setNewNoteContent(''); // Clear the input field
        } catch (error) {
            console.error('Error creating note:', error);
            setError('Error creating note');
        }
    };

    // Handle deleting a note
    const handleDeleteNote = async (noteId) => {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                navigate('/login');
                return;
            }

            await NoteService.deleteNote(noteId, token);
            setNotes(notes.filter(note => note.id !== noteId));  // Remove the deleted note
        } catch (error) {
            console.error('Error deleting note:', error);
            setError('Error deleting note');
        }
    };

    // Handle updating a note
    const handleUpdateNote = async (noteId, newContent) => {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                navigate('/login');
                return;
            }
    
            const response = await NoteService.updateNote(noteId, { content: newContent }, token);
            setNotes(notes.map(note => note.id === noteId ? response.data : note));  // Update the note
        } catch (error) {
            console.error('Error updating note:', error);
            setError('Error updating note');
        }
    };
    

    return (
        <div className="notes-list-container">
            <h2>Notes</h2>
            {error && <p className="error-message">{error}</p>}

            {/* Create a new note */}
            <div className="new-note-container">
                <textarea
                    value={newNoteContent}
                    onChange={(e) => setNewNoteContent(e.target.value)}
                    placeholder="Write a new note"
                />
                <button onClick={handleCreateNote}>Create Note</button>
            </div>

            <ul>
                {notes.map(note => (
                    <li key={note.id}>
                        <div>
                            <p>{note.content}</p>
                            <small>By: {note.user.name}</small>
                            <div className="note-actions">
                                {/* Update button */}
                                <button onClick={() => {
                                    const newContent = prompt('Edit note content:', note.content);
                                    if (newContent) {
                                        handleUpdateNote(note.id, newContent);
                                    }
                                }}>
                                    Edit
                                </button>

                                {/* Delete button */}
                                <button onClick={() => handleDeleteNote(note.id)}>Delete</button>
                            </div>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default NotesList;
