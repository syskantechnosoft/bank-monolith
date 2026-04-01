import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';

const ProtectedRoute = ({ children, allowedRoles }) => {
    const token = localStorage.getItem('token');
    const userString = localStorage.getItem('user');
    const location = useLocation();

    if (!token || !userString) {
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    try {
        const user = JSON.parse(userString);
        if (allowedRoles && !allowedRoles.includes(user.role)) {
            // User is authenticated but doesn't have the required role
            return <Navigate to="/unauthorized" replace />;
        }
    } catch (error) {
        console.error("Invalid user data in session");
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        return <Navigate to="/login" replace />;
    }

    return children;
};

export default ProtectedRoute;
