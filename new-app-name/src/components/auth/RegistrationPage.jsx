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
        setErrors(tempErrors);
        return Object.keys(tempErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!validateForm()) {
            alert('Please correct the errors in the form.');
            return;
        }

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
