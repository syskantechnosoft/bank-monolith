import React from 'react';
import { Outlet, Link, useNavigate } from 'react-router-dom';
import { LogOut, Home, User as UserIcon } from 'lucide-react';

const MainLayout = () => {
    const navigate = useNavigate();
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        navigate('/login');
    };

    return (
        <div className="min-h-screen flex flex-col">
            <nav className="bg-primary-600 text-white shadow-md">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16">
                        <div className="flex items-center space-x-4">
                            <Link to="/" className="flex items-center gap-2 text-xl font-bold bg-white/10 px-3 py-2 rounded shadow-sm hover:bg-white/20 transition-all">
                                <Home size={24} />
                                Bank Monolith
                            </Link>
                            {token && user.role === 'ROLE_CUSTOMER' && (
                                <Link to="/customer/dashboard" className="px-3 py-2 rounded-md hover:bg-primary-500">
                                    Dashboard
                                </Link>
                            )}
                            {token && user.role === 'ROLE_MANAGER' && (
                                <Link to="/manager/dashboard" className="px-3 py-2 rounded-md hover:bg-primary-500">
                                    Manager Hub
                                </Link>
                            )}
                            {token && user.role === 'ROLE_ADMIN' && (
                                <Link to="/admin/dashboard" className="px-3 py-2 rounded-md hover:bg-primary-500">
                                    Admin Panel
                                </Link>
                            )}
                        </div>

                        <div className="flex items-center space-x-4">
                            {token ? (
                                <div className="flex items-center space-x-4">
                                    <span className="flex items-center gap-2 text-primary-100 p-2 rounded bg-primary-700/50">
                                        <UserIcon size={18} /> Hello, {user.email?.split('@')[0]}
                                    </span>
                                    <button
                                        onClick={handleLogout}
                                        className="flex items-center gap-2 px-4 py-2 text-sm font-medium bg-red-500 hover:bg-red-600 rounded-md transition-colors"
                                    >
                                        <LogOut size={16} /> Logout
                                    </button>
                                </div>
                            ) : (
                                <div className="space-x-2">
                                    <Link to="/login" className="px-4 py-2 hover:bg-primary-500 rounded-md">
                                        Login
                                    </Link>
                                    <Link to="/register" className="px-4 py-2 bg-white text-primary-600 hover:bg-gray-100 rounded-md font-medium">
                                        Register
                                    </Link>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </nav>

            <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <Outlet />
            </main>

            <footer className="bg-white border-t border-gray-200 py-6 text-center text-gray-500 text-sm">
                <p>&copy; {new Date().getFullYear()} Bank Monolith Application. All rights reserved.</p>
            </footer>
        </div>
    );
};

export default MainLayout;
