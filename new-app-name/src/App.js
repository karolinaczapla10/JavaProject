import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Navbar from './components/common/Navbar';
import LoginPage from './components/auth/LoginPage';
import RegistrationPage from './components/auth/RegistrationPage';
import FooterComponent from './components/common/Footer';
import UserService from './components/service/UserService';
import UpdateUser from './components/userspage/UpdateUser';
import UserManagementPage from './components/userspage/UserManagementPage';
import ProfilePage from './components/userspage/ProfilePage';
import NotesList from './components/auth/NotesList';

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(UserService.isAuthenticated());
    const [isAdmin, setIsAdmin] = useState(UserService.adminOnly());

    useEffect(() => {
        // Aktualizacja stanu po zalogowaniu/wylogowaniu
        const updateAuthStatus = () => {
            setIsAuthenticated(UserService.isAuthenticated());
            setIsAdmin(UserService.adminOnly());
        };

        window.addEventListener('authChange', updateAuthStatus); // Nasłuchiwanie zmian autoryzacji

        return () => {
            window.removeEventListener('authChange', updateAuthStatus); 
        };
    }, []);

    return (
        <BrowserRouter>
            <div className="App">
                <Navbar />
                <div className="content">
                    <Routes>
                        <Route exact path="/" element={<LoginPage />} />
                        <Route exact path="/login" element={<LoginPage />} />

                        {isAuthenticated && <Route path="/profile" element={<ProfilePage />} />}
                        {isAuthenticated && <Route path="/notes" element={<NotesList />} />}

                        {isAdmin && (
                            <>
                                <Route path="/register" element={<RegistrationPage />} />
                                <Route path="/admin/user-management" element={<UserManagementPage />} />
                                <Route path="/update-user/:userId" element={<UpdateUser />} />
                            </>
                        )}
                        <Route path="*" element={<Navigate to="/login" />} />
                    </Routes>
                </div>
                <FooterComponent />
            </div>
        </BrowserRouter>
    );
}

export default App;
