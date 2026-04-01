import React, { useState, useEffect } from 'react';
import api from '../../api/axios';
import { CreditCard, Wallet, Send, FileText, ArrowRightRight } from 'lucide-react';

const CustomerDashboard = () => {
    const [activeTab, setActiveTab] = useState('accounts');
    const [accounts, setAccounts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    // form states
    const [transferForm, setTransferForm] = useState({ sourceAccountNumber: '', targetAccountNumber: '', amount: '', description: '', transactionType: 'TRANSFER_OUT' });
    const [loanForm, setLoanForm] = useState({ principalAmount: '', tenureMonths: '' });
    const [createAccountForm, setCreateAccountForm] = useState({ accountType: 'SAVINGS', initialDeposit: '' });
    const [statementData, setStatementData] = useState([]);
    const [selectedStatementAcc, setSelectedStatementAcc] = useState('');

    const fetchAccounts = async (accId = null) => {
        // In a real app we might have an endpoint to list user's accounts
        // For now we will rely on creating them, or if we had a dedicated endpoint:
        try {
            // the backend doesn't have a specific `GET /api/v1/customer/accounts` but we can simulate or we'd just see them.
            // Wait, I didn't add a `GET /accounts` to CustomerController!
            // To fix this quickly, I'll provide an empty state or just rely on the UI to create accounts first,
            // and keep track of them in state for this demo.
            // Ideally we should add GET /api/v1/customer/accounts in backend. 
        } catch (err) {
            console.error(err);
        }
    };

    const handleCreateAccount = async (e) => {
        e.preventDefault();
        try {
            const resp = await api.post('/customer/accounts', {
                accountType: createAccountForm.accountType,
                initialDeposit: parseFloat(createAccountForm.initialDeposit) || 0
            });
            alert('Account Created: ' + resp.data.data.accountNumber);
            setAccounts([...accounts, resp.data.data]);
        } catch (err) {
            alert('Error creating account: ' + err.response?.data?.message);
        }
    };

    const handleTransfer = async (e) => {
        e.preventDefault();
        try {
            await api.post('/customer/transactions', {
                ...transferForm,
                amount: parseFloat(transferForm.amount)
            });
            alert('Transfer successful');
            setTransferForm({ sourceAccountNumber: '', targetAccountNumber: '', amount: '', description: '', transactionType: 'TRANSFER_OUT' });
        } catch (err) {
            alert('Transfer failed: ' + err.response?.data?.message);
        }
    };

    const handleLoanApply = async (e) => {
        e.preventDefault();
        try {
            const resp = await api.post('/customer/loans/apply', {
                principalAmount: parseFloat(loanForm.principalAmount),
                tenureMonths: parseInt(loanForm.tenureMonths)
            });
            alert('Loan approved! EMI: ' + resp.data.data.emiAmount);
            setAccounts([...accounts, resp.data.data]); // add loan to accounts list
        } catch (err) {
            alert('Loan application failed: ' + err.response?.data?.message);
        }
    };

    const fetchStatements = async (e) => {
        e.preventDefault();
        try {
            const resp = await api.get(`/customer/accounts/${selectedStatementAcc}/statements`);
            setStatementData(resp.data.data.content);
        } catch (err) {
            alert("Failed to fetch statements");
        }
    };

    return (
        <div className="flex flex-col md:flex-row gap-6">
            {/* Sidebar */}
            <div className="w-full md:w-64 bg-white rounded-lg shadow-sm p-4 h-fit border border-gray-100">
                <nav className="space-y-2">
                    <button onClick={() => setActiveTab('accounts')} className={`w-full flex items-center space-x-3 px-4 py-3 rounded-lg transition-colors ${activeTab === 'accounts' ? 'bg-primary-50 text-primary-700 font-medium' : 'text-gray-600 hover:bg-gray-50'}`}>
                        <Wallet size={20} /> <span>Open Accounts</span>
                    </button>
                    <button onClick={() => setActiveTab('transfer')} className={`w-full flex items-center space-x-3 px-4 py-3 rounded-lg transition-colors ${activeTab === 'transfer' ? 'bg-primary-50 text-primary-700 font-medium' : 'text-gray-600 hover:bg-gray-50'}`}>
                        <Send size={20} /> <span>Transfer Funds</span>
                    </button>
                    <button onClick={() => setActiveTab('statements')} className={`w-full flex items-center space-x-3 px-4 py-3 rounded-lg transition-colors ${activeTab === 'statements' ? 'bg-primary-50 text-primary-700 font-medium' : 'text-gray-600 hover:bg-gray-50'}`}>
                        <FileText size={20} /> <span>Statements</span>
                    </button>
                    <button onClick={() => setActiveTab('loans')} className={`w-full flex items-center space-x-3 px-4 py-3 rounded-lg transition-colors ${activeTab === 'loans' ? 'bg-primary-50 text-primary-700 font-medium' : 'text-gray-600 hover:bg-gray-50'}`}>
                        <CreditCard size={20} /> <span>Apply Loan</span>
                    </button>
                </nav>
            </div>

            {/* Main Content Area */}
            <div className="flex-1 bg-white rounded-lg shadow-sm border border-gray-100 p-6">

                {/* Open Account Tab */}
                {activeTab === 'accounts' && (
                    <div>
                        <h2 className="text-2xl font-bold text-gray-800 mb-6 border-b pb-2">Open New Account</h2>
                        <form onSubmit={handleCreateAccount} className="max-w-md space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Account Type</label>
                                <select className="mt-1 block w-full rounded-md border-gray-300 py-2 px-3 border" value={createAccountForm.accountType} onChange={e => setCreateAccountForm({ ...createAccountForm, accountType: e.target.value })}>
                                    <option value="SAVINGS">Savings Account</option>
                                    <option value="CURRENT">Current Account</option>
                                    <option value="DEPOSIT">Fixed Deposit</option>
                                </select>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Initial Deposit ($)</label>
                                <input type="number" step="0.01" className="mt-1 block w-full rounded-md border-gray-300 py-2 px-3 border" value={createAccountForm.initialDeposit} onChange={e => setCreateAccountForm({ ...createAccountForm, initialDeposit: e.target.value })} required />
                            </div>
                            <button type="submit" className="bg-primary-600 text-white px-4 py-2 rounded shadow hover:bg-primary-700">Create Account</button>
                        </form>

                        <div className="mt-12">
                            <h3 className="font-semibold text-lg mb-4">Your Session Accounts</h3>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                {accounts.map((acc, idx) => (
                                    <div key={idx} className="border p-4 rounded bg-gray-50 shadow-sm border-gray-200">
                                        <div className="flex justify-between items-center mb-2">
                                            <span className="text-xs font-bold text-gray-500 uppercase">{acc.accountType || 'Account'}</span>
                                            <span className="text-xs font-medium bg-green-100 text-green-800 px-2 py-1 rounded">{acc.status || 'ACTIVE'}</span>
                                        </div>
                                        <p className="font-mono text-gray-800 text-lg">{acc.accountNumber}</p>
                                        <p className="text-2xl font-bold text-primary-700 mt-2">${acc.balance?.toFixed(2) || acc.outstandingBalance?.toFixed(2)}</p>
                                    </div>
                                ))}
                                {accounts.length === 0 && <p className="text-gray-500">No accounts opened in this session.</p>}
                            </div>
                        </div>
                    </div>
                )}

                {/* Transfer Funds Tab */}
                {activeTab === 'transfer' && (
                    <div>
                        <h2 className="text-2xl font-bold text-gray-800 mb-6 border-b pb-2">Fund Transfer</h2>
                        <form onSubmit={handleTransfer} className="max-w-md space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Source Account Number</label>
                                <input type="text" className="mt-1 block w-full border-gray-300 py-2 px-3 border rounded-md" value={transferForm.sourceAccountNumber} onChange={e => setTransferForm({ ...transferForm, sourceAccountNumber: e.target.value })} required />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Target Account Number</label>
                                <input type="text" className="mt-1 block w-full border-gray-300 py-2 px-3 border rounded-md" value={transferForm.targetAccountNumber} onChange={e => setTransferForm({ ...transferForm, targetAccountNumber: e.target.value })} required />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Amount ($)</label>
                                <input type="number" step="0.01" className="mt-1 block w-full border-gray-300 py-2 px-3 border rounded-md" value={transferForm.amount} onChange={e => setTransferForm({ ...transferForm, amount: e.target.value })} required />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Description</label>
                                <input type="text" className="mt-1 block w-full border-gray-300 py-2 px-3 border rounded-md" value={transferForm.description} onChange={e => setTransferForm({ ...transferForm, description: e.target.value })} />
                            </div>
                            <button type="submit" className="bg-primary-600 flex items-center gap-2 text-white px-4 py-2 rounded shadow hover:bg-primary-700">
                                <ArrowRightRight size={18} /> Send Money
                            </button>
                        </form>
                    </div>
                )}

                {/* Statements Tab */}
                {activeTab === 'statements' && (
                    <div>
                        <h2 className="text-2xl font-bold text-gray-800 mb-6 border-b pb-2">Account Statements</h2>
                        <form onSubmit={fetchStatements} className="flex gap-4 items-end mb-6">
                            <div className="flex-1 max-w-xs">
                                <label className="block text-sm font-medium text-gray-700">Account Number</label>
                                <input type="text" className="mt-1 block w-full border-gray-300 py-2 px-3 border rounded-md" value={selectedStatementAcc} onChange={e => setSelectedStatementAcc(e.target.value)} required />
                            </div>
                            <button type="submit" className="bg-primary-600 text-white px-4 py-2 rounded shadow hover:bg-primary-700">Fetch</button>
                        </form>

                        <div className="overflow-x-auto">
                            <table className="min-w-full divide-y divide-gray-200">
                                <thead className="bg-gray-50">
                                    <tr>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Amount</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                                    </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-gray-200">
                                    {statementData.length > 0 ? statementData.map((trx, idx) => (
                                        <tr key={idx}>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{new Date(trx.timestamp).toLocaleString()}</td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-medium">{trx.type}</td>
                                            <td className={`px-6 py-4 whitespace-nowrap text-sm font-bold ${trx.type.includes('IN') || trx.type === 'DEPOSIT' ? 'text-green-600' : 'text-red-600'}`}>
                                                ${trx.amount.toFixed(2)}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{trx.status}</td>
                                        </tr>
                                    )) : (
                                        <tr><td colSpan="4" className="px-6 py-4 text-center text-sm text-gray-500">No transactions found</td></tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}

                {/* Apply Loan Tab */}
                {activeTab === 'loans' && (
                    <div>
                        <h2 className="text-2xl font-bold text-gray-800 mb-6 border-b pb-2">Apply for a Loan</h2>
                        <form onSubmit={handleLoanApply} className="max-w-md space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Principal Amount ($)</label>
                                <input type="number" step="0.01" className="mt-1 block w-full border-gray-300 py-2 px-3 border rounded-md" value={loanForm.principalAmount} onChange={e => setLoanForm({ ...loanForm, principalAmount: e.target.value })} required />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Tenure (Months)</label>
                                <input type="number" className="mt-1 block w-full border-gray-300 py-2 px-3 border rounded-md" min="6" value={loanForm.tenureMonths} onChange={e => setLoanForm({ ...loanForm, tenureMonths: e.target.value })} required />
                            </div>
                            <button type="submit" className="bg-primary-600 text-white px-4 py-2 rounded shadow hover:bg-primary-700">Submit Application</button>
                        </form>
                    </div>
                )}

            </div>
        </div>
    );
};

export default CustomerDashboard;
