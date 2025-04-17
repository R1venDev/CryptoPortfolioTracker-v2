import React, { useEffect, useState } from 'react';
import axios from '../apis/cryptoPortfolio'; // Adjust import path to your API utility
import { APIKey } from '../apis/apiKey'; // Adjust import path to your API key
import { useNavigate } from 'react-router-dom'; // Import useNavigate from react-router-dom

const AdminPage = () => {
    const [users, setUsers] = useState([]);
    const [blockedUsers, setBlockedUsers] = useState([]);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        // Fetch all users and blocked users
        const fetchData = async () => {
            try {
                const [usersResponse, blockedResponse] = await Promise.all([
                    axios.get('/get-users', { headers: { 'x-api-key': APIKey } }),
                    axios.get('/get-blocked-users', { headers: { 'x-api-key': APIKey } }),
                ]);
                console.log(usersResponse.data);
                console.log(blockedResponse.data);
                setUsers(usersResponse.data);
                setBlockedUsers(blockedResponse.data);
            } catch (error) {
                setError('Failed to load data');
            }
        };

        fetchData();
    }, []);

    // Handle Block/Delete button click
    const handleAction = async (username, action) => {
        try {
            const endpoint = action === 'block'
                ? `/blockUser/${username}`
                : `/deleteUser/${username}`;

            const method = action === 'block' ? 'POST' : 'DELETE';

            const config = {
                method,
                url: endpoint,
                headers: { 'x-api-key': APIKey },
            };

            await axios(config);

            alert(`${action === 'block' ? 'Blocked' : 'Deleted'} user successfully`);
            if (action === 'block') {
                setBlockedUsers(prevBlockedUsers => [...prevBlockedUsers, { username: username }]);
            }
            setUsers(prevUsers => prevUsers.filter(user => user.username !== username));
        } catch (error) {
            alert(`Failed to ${action} user`);
            console.error(error);
        }
    };

    // Handle deleting a blocked user
    const handleDeleteBlockedUser = async (username) => {
        try {
            const config = {
                method: 'DELETE',
                url: `/deleteUser/${username}`,  // Assuming this is your endpoint
                headers: { 'x-api-key': APIKey },
            };

            await axios(config);

            alert(`Deleted blocked user: ${username}`);
            setBlockedUsers(prevBlockedUsers => prevBlockedUsers.filter(user => user.username !== username));
        } catch (error) {
            alert(`Failed to delete blocked user: ${username}`);
            console.error(error);
        }
    };

    // Handle giving admin rights to a user
    const handleGiveAdminRights = async (username) => {
        try {
            const config = {
                method: 'PUT',  // Use PUT or PATCH for updating resources
                url: `/give-admin-rights/${username}`,
                headers: {
                    'x-api-key': APIKey,
                    'Content-Type': 'application/json',
                },
            };

            await axios(config);

            alert(`Admin rights given to user: ${username}`);
            setUsers(prevUsers => prevUsers.map(user =>
                user.username === username ? { ...user, adminRight: true } : user
            ));
        } catch (error) {
            alert(`Failed to give admin rights to ${username}`);
            console.error(error);
        }
    };

    const logoutHandler = () => {
        navigate('/login');
    };

    return (
        <div style={{ padding: '20px' }}>
            <h2>Welcome to the Admin Page</h2>
            <p>Manage the application here.</p>

            {error && <p className="error" style={{ color: 'red' }}>{error}</p>}

            <div style={{ display: 'flex', justifyContent: 'space-around', marginBottom: '30px' }}>
                {/* Administrators Table */}
                <div>
                    <h3>Administrators</h3>
                    <table border="1" style={{ borderCollapse: 'collapse', width: '300px' }}>
                        <thead>
                        <tr>
                            <th>Username</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        {users.filter(user => user.adminRight).map(admin => (
                            <tr key={admin.username}>
                                <td>{admin.username}</td>
                                <td>
                                    <button onClick={() => handleAction(admin.username, 'delete')}>Delete</button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>

                {/* Clients Table */}
                <div>
                    <h3>Clients</h3>
                    <table border="1" style={{ borderCollapse: 'collapse', width: '300px' }}>
                        <thead>
                        <tr>
                            <th>Username</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        {users.filter(user => !user.adminRight).map(client => (
                            <tr key={client.username}>
                                <td>{client.username}</td>
                                <td>
                                    <button onClick={() => handleAction(client.username, 'block')} style={buttonStyle}>Block</button>
                                    <button onClick={() => handleAction(client.username, 'delete')} style={buttonStyle}>Delete</button>
                                    <button onClick={() => handleGiveAdminRights(client.username)} style={{ ...buttonStyle, backgroundColor: 'green' }}>Give Admin Rights</button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Blocked Users Table - Centered and with gap */}
            <div style={{ textAlign: 'center', marginTop: '40px' }}>
                <h3>Blocked Users</h3>
                <table border="1" style={{ borderCollapse: 'collapse', width: '300px', margin: '0 auto' }}>
                    <thead>
                    <tr>
                        <th>Username</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    {blockedUsers.map(user => (
                        <tr key={user.username}>
                            <td>{user.username}</td>
                            <td>
                                <button onClick={() => handleDeleteBlockedUser(user.username)} style={buttonStyle}>Delete</button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            {/* Logout Button */}
            <button
                onClick={logoutHandler}
                style={{
                    position: 'fixed',
                    bottom: '20px',
                    left: '20px',
                    padding: '10px 20px',
                    fontSize: '16px',
                    backgroundColor: '#ff4d4d',
                    color: 'white',
                    border: 'none',
                    borderRadius: '5px',
                    cursor: 'pointer',
                }}
            >
                Logout
            </button>
        </div>
    );
};

const buttonStyle = {
    width: '100%',
    padding: '10px',
    fontSize: '16px',
    backgroundColor: '#ff4d4d',
    color: 'white',
    border: 'none',
    borderRadius: '5px',
    cursor: 'pointer',
};

export default AdminPage;
