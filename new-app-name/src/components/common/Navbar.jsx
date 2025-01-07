// src/components/common/Navbar.jsx
import { Link } from 'react-router-dom';
import UserService from '../service/UserService';

function Navbar() {
    const isAuthenticated = UserService.isAuthenticated();
    const isAdmin = UserService.isAdmin();

    const handleLogout = () => {
        const confirmDelete = window.confirm('Are you sure you want to logout this user?');
        if (confirmDelete) {
            UserService.logout();
        }
    };

    return (
        <nav>
            <ul>
                {!isAuthenticated && <li><Link to="/">App</Link></li>}
                {isAuthenticated && <li><Link to="/profile">Profile</Link></li>}
                {isAuthenticated && <li><Link to="/notes">Your Notes</Link></li>} {/* Added Notes link */}
                {isAdmin && <li><Link to="/admin/user-management">User Management</Link></li>}
                {isAuthenticated && <li><Link to="/" onClick={handleLogout}>Logout</Link></li>}
            </ul>
        </nav>
    );
}

export default Navbar;
