import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/layout/MainLayout';
import ProtectedRoute from './components/auth/ProtectedRoute';

import Login from './components/auth/Login';
import Register from './components/auth/Register';
import CustomerDashboard from './components/dashboard/CustomerDashboard';
import AdminDashboard from './components/dashboard/AdminDashboard';
import ManagerDashboard from './components/dashboard/ManagerDashboard';

// Placeholder Pages - Will implement these next
const Unauthorized = () => <div className="p-8 text-center text-red-500"><h1 className="text-2xl font-bold">403 - Unauthorized access</h1><p>You do not have permission to view this page.</p></div>;

const HomeRedirect = () => {
  const userString = localStorage.getItem('user');
  if (!userString) return <Navigate to="/login" />;

  try {
    const user = JSON.parse(userString);
    if (user.role === 'ROLE_CUSTOMER') return <Navigate to="/customer/dashboard" />;
    if (user.role === 'ROLE_MANAGER') return <Navigate to="/manager/dashboard" />;
    if (user.role === 'ROLE_ADMIN') return <Navigate to="/admin/dashboard" />;
  } catch {
    return <Navigate to="/login" />;
  }
  return <Navigate to="/login" />;
};

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainLayout />}>
          {/* Public Routes */}
          <Route index element={<HomeRedirect />} />
          <Route path="login" element={<Login />} />
          <Route path="register" element={<Register />} />
          <Route path="unauthorized" element={<Unauthorized />} />

          {/* Customer Routes */}
          <Route path="customer/dashboard" element={
            <ProtectedRoute allowedRoles={['ROLE_CUSTOMER']}>
              <CustomerDashboard />
            </ProtectedRoute>
          } />

          {/* Manager Routes */}
          <Route path="manager/dashboard" element={
            <ProtectedRoute allowedRoles={['ROLE_MANAGER', 'ROLE_ADMIN']}>
              <ManagerDashboard />
            </ProtectedRoute>
          } />

          {/* Admin Routes */}
          <Route path="admin/dashboard" element={
            <ProtectedRoute allowedRoles={['ROLE_ADMIN']}>
              <AdminDashboard />
            </ProtectedRoute>
          } />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
