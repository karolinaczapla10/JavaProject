import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import UserService from '../service/UserService';

function UpdateUser() {
  const navigate = useNavigate();
  const { userId } = useParams();


  const [userData, setUserData] = useState({
    name: '',
    email: '',
    role: '',
    city: ''
  });

  const [error, setError] = useState('');


  useEffect(() => {
    fetchUserDataById(userId); // Pass the userId to fetchUserDataById
  }, [userId]); //wheen ever there is a chane in userId, run this

  const fetchUserDataById = async (userId) => {
    try {
      const token = localStorage.getItem('token');
      const response = await UserService.getUserById(userId, token); // Pass userId to getUserById
      const { name, email, role, city } = response.ourUsers;
      setUserData({ name, email, role, city });
    } catch (error) {
      console.error('Error fetching user data:', error);
    }
  };


  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setUserData((prevUserData) => ({
      ...prevUserData,
      [name]: value
    }));
  };

  const validateForm = () => {
    if (!userData.name.trim()) {
      setError('Name cannot be empty.');
      return false;
    }
    if (!userData.city.trim()) {
      setError('City cannot be empty.');
      return false;
    }
    if (!userData.email.includes('@')) {
      setError('Invalid email address.');
      return false;
    }
    if (userData.role !== 'USER' && userData.role !== 'ADMIN') {
      setError('Role must be either USER or ADMIN.');
      return false;
    }
    setError(''); // Clear errors if all validations pass
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) {
        return; // Stop submission if validation fails
      }
    try {
      const confirmDelete = window.confirm('Are you sure you want to delete this user?');
      if (confirmDelete) {
        const token = localStorage.getItem('token');
        const res = await UserService.updateUser(userId, userData, token);
        console.log(res)
        // Redirect to profile page or display a success message
        navigate("/admin/user-management")
      }

    } catch (error) {
      console.error('Error updating user profile:', error);
      alert(error)
    }
  };

  return (
    <div className="auth-container">
      <h2>Update User</h2>
      {error && <p className="error-message" style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Name:</label>
          <input type="text" name="name" value={userData.name} onChange={handleInputChange} />
        </div>
        <div className="form-group">
          <label>Email:</label>
          <input type="email" name="email" value={userData.email} onChange={handleInputChange} />
        </div>
        <div className="form-group">
          <label>Role:</label>
          <input type="text" name="role" value={userData.role} onChange={handleInputChange} />
        </div>
        <div className="form-group">
          <label>City:</label>
          <input type="text" name="city" value={userData.city} onChange={handleInputChange} />
        </div>
        <button type="submit">Update</button>
      </form>
    </div>
  );
}

export default UpdateUser;