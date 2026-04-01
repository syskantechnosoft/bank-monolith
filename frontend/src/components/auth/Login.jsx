import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../api/axios';
import { Lock, Mail } from 'lucide-react';

const Login = () => {
    const [formData, setFormData] = useState({ email: '', password: '' });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const response = await api.post('/auth/login', formData);
            const data = response.data.data; // ApiResponse wrapper
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify({ email: data.email, role: data.role, id: data.userId }));

            // Redirect based on role
            if (data.role === 'ROLE_CUSTOMER') navigate('/customer/dashboard');
            else if (data.role === 'ROLE_MANAGER') navigate('/manager/dashboard');
            else if (data.role === 'ROLE_ADMIN') navigate('/admin/dashboard');
            else navigate('/');

            // refresh window to update navbar state
            window.location.reload();
        } catch (err) {
            setError(err.response?.data?.message || 'Login failed. Please check your credentials.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex items-center justify-center min-h-[70vh]">
            <div className="w-full max-w-md bg-white rounded-lg shadow-xl p-8 transform transition-all duration-300 hover:shadow-2xl">
                <h2 className="text-3xl font-extrabold text-center text-primary-900 mb-8">Welcome Back</h2>

                {error && (
                    <div className="bg-red-50 text-red-500 p-3 rounded-md mb-6 text-sm border border-red-200">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-6">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Email Address</label>
                        <div className="mt-1 relative rounded-md shadow-sm">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
                                <Mail size={18} />
                            </div>
                            <input
                                type="email"
                                name="email"
                                required
                                className="pl-10 block w-full rounded-md border-gray-300 border focus:ring-primary-500 focus:border-primary-500 sm:text-sm py-2 px-3"
                                placeholder="you@example.com"
                                value={formData.email}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700">Password</label>
                        <div className="mt-1 relative rounded-md shadow-sm">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
                                <Lock size={18} />
                            </div>
                            <input
                                type="password"
                                name="password"
                                required
                                className="pl-10 block w-full rounded-md border-gray-300 border focus:ring-primary-500 focus:border-primary-500 sm:text-sm py-2 px-3"
                                placeholder="********"
                                value={formData.password}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full flex justify-center py-2.5 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 transition-colors disabled:opacity-50"
                    >
                        {loading ? 'Signing in...' : 'Sign in'}
                    </button>
                </form>

                <div className="mt-6 text-center text-sm">
                    <span className="text-gray-600">Don't have an account? </span>
                    <Link to="/register" className="font-medium text-primary-600 hover:text-primary-500">
                        Register here
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default Login;
