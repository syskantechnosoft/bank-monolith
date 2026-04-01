import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../api/axios';

const Register = () => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        phoneNumber: ''
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const response = await api.post('/auth/register', formData);
            const data = response.data.data;
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify({ email: data.email, role: data.role, id: data.userId }));

            navigate('/customer/dashboard');
            window.location.reload();
        } catch (err) {
            if (err.response?.data?.errors) {
                // specific validation errors
                setError(Object.values(err.response.data.errors).join(", "));
            } else {
                setError(err.response?.data?.message || 'Registration failed.');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex items-center justify-center min-h-[70vh]">
            <div className="w-full max-w-lg bg-white rounded-lg shadow-xl p-8">
                <h2 className="text-3xl font-extrabold text-center text-primary-900 mb-8">Create an Account</h2>

                {error && (
                    <div className="bg-red-50 text-red-500 p-3 rounded-md mb-6 text-sm border border-red-200">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700">First Name</label>
                            <input type="text" name="firstName" required className="mt-1 block w-full rounded-md border-gray-300 border focus:ring-primary-500 focus:border-primary-500 sm:text-sm py-2 px-3" onChange={handleChange} />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Last Name</label>
                            <input type="text" name="lastName" required className="mt-1 block w-full rounded-md border-gray-300 border focus:ring-primary-500 focus:border-primary-500 sm:text-sm py-2 px-3" onChange={handleChange} />
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700">Email Address</label>
                        <input type="email" name="email" required className="mt-1 block w-full rounded-md border-gray-300 border focus:ring-primary-500 focus:border-primary-500 sm:text-sm py-2 px-3" onChange={handleChange} />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700">Phone Number</label>
                        <input type="tel" name="phoneNumber" required placeholder="+1234567890" className="mt-1 block w-full rounded-md border-gray-300 border focus:ring-primary-500 focus:border-primary-500 sm:text-sm py-2 px-3" onChange={handleChange} />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700">Password</label>
                        <input type="password" name="password" required minLength="8" className="mt-1 block w-full rounded-md border-gray-300 border focus:ring-primary-500 focus:border-primary-500 sm:text-sm py-2 px-3" onChange={handleChange} />
                        <p className="text-xs text-gray-500 mt-1">Must be at least 8 characters long.</p>
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full flex justify-center py-2.5 px-4 mt-6 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 focus:outline-none transition-colors disabled:opacity-50"
                    >
                        {loading ? 'Registering...' : 'Complete Registration'}
                    </button>
                </form>

                <div className="mt-6 text-center text-sm">
                    <span className="text-gray-600">Already have an account? </span>
                    <Link to="/login" className="font-medium text-primary-600 hover:text-primary-500">
                        Sign in
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default Register;
