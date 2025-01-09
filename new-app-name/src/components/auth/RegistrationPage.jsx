import React, { useState } from 'react';
import UserService from '../service/UserService';
import { useNavigate } from 'react-router-dom';

function RegistrationPage() {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
        role: '',
        city: ''
    });

    const [errors, setErrors] = useState({});
    const [emailExists, setEmailExists] = useState(false); // Add emailExists state to track email validation

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const validateForm = () => {
        let tempErrors = {};
        if (formData.password.length < 3) {
            tempErrors.password = 'Password must be at least 3 characters long.';
        }
        if (!formData.email.includes('@')) {
            tempErrors.email = 'Email must contain @.';
        }
        if (!['USER', 'ADMIN'].includes(formData.role.toUpperCase())) {
            tempErrors.role = 'Role must be either USER or ADMIN.';
        }
        if (!formData.name) {
            tempErrors.name = 'Name is required.';
        }
        if (!formData.city) {
            tempErrors.city = 'City is required.';
        }
        setErrors(tempErrors);
        return Object.keys(tempErrors).length === 0;
    };

    const checkEmailExists = async () => {
        try {
            const emailInUse = await UserService.checkEmailExists(formData.email);
            if (emailInUse) {
                setEmailExists(true);
                return false; // If email exists, return false to stop form submission
            }
            setEmailExists(false);
            return true; // If email doesn't exist, return true
        } catch (err) {
            console.error('Error checking email existence:', err);
            return false;
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // First validate the form
        if (!validateForm()) {
            alert('Please correct the errors in the form.');
            return;
        }

        // Check if email already exists
        const isEmailAvailable = await checkEmailExists();
        if (!isEmailAvailable) {
            alert('Email is already in use, please provide a different email.');
            return;
        }

        // Proceed with the registration if email is valid
        try {
            const token = localStorage.getItem('token');
            await UserService.register(formData, token);

            setFormData({
                name: '',
                email: '',
                password: '',
                role: '',
                city: ''
            });
            alert('User registered successfully');
            navigate('/admin/user-management');

        } catch (error) {
            console.error('Error registering user:', error);
            alert('An error occurred while registering user');
        }
    };

    return (
        <div className="auth-container">
            <h2>Registration</h2>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Name:</label>
                    <input type="text" name="name" value={formData.name} onChange={handleInputChange} required />
                </div>

                <div className="form-group">
                    <label>Email:</label>
                    <input type="email" name="email" value={formData.email} onChange={handleInputChange} required />
                    {emailExists && <p style={{ color: 'red' }}>This email is already in use. Please provide a different email.</p>}
                    {errors.email && <p style={{ color: 'red' }}>{errors.email}</p>}
                </div>

                <div className="form-group">
                    <label>Password:</label>
                    <input type="password" name="password" value={formData.password} onChange={handleInputChange} required />
                    {errors.password && <p style={{ color: 'red' }}>{errors.password}</p>}
                </div>

                <div className="form-group">
                    <label>Role:</label>
                    <input type="text" name="role" value={formData.role} onChange={handleInputChange} required />
                    {errors.role && <p style={{ color: 'red' }}>{errors.role}</p>}
                </div>

                <div className="form-group">
                    <label>City:</label>
                    <input type="text" name="city" value={formData.city} onChange={handleInputChange} required />
                </div>

                <button type="submit">Register</button>
            </form>
        </div>
    );
}

export default RegistrationPage;
