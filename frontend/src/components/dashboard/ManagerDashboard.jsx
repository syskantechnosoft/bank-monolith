import React, { useState, useEffect } from 'react';
import api from '../../api/axios';
import { Briefcase } from 'lucide-react';

const ManagerDashboard = () => {
    const [accounts, setAccounts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchAccounts = async () => {
            try {
                const response = await api.get('/manager/accounts');
                setAccounts(response.data.data);
            } catch (err) {
                console.error("Failed to fetch accounts", err);
            } finally {
                setLoading(false);
            }
        };
        fetchAccounts();
    }, []);

    return (
        <div className="bg-white rounded-lg shadow-sm border border-gray-100 p-6">
            <div className="flex items-center space-x-3 border-b pb-4 mb-6">
                <Briefcase size={28} className="text-primary-600" />
                <h1 className="text-2xl font-bold text-gray-800">Manager Dashboard - Global Accounts</h1>
            </div>

            {loading ? (
                <p className="text-gray-500">Loading accounts...</p>
            ) : (
                <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200 shadow-sm rounded-lg overflow-hidden border">
                        <thead className="bg-gray-50">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Account No</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Balance</th>
                            </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                            {accounts.map(acc => (
                                <tr key={acc.id}>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm font-mono text-gray-900">{acc.accountNumber}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-700">{acc.accountType}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm">
                                        <span className="px-2 py-1 rounded-full text-xs font-bold bg-green-100 text-green-800">
                                            {acc.status}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-bold">${acc.balance ? acc.balance.toFixed(2) : '0.00'}</td>
                                </tr>
                            ))}
                            {accounts.length === 0 && <tr><td colSpan="4" className="px-6 py-4 text-center text-sm text-gray-500">No accounts found in entire system.</td></tr>}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default ManagerDashboard;
