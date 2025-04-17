import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';  // Import Link here
import { setUserSession } from '../service/authService';
import axios from '../apis/cryptoPortfolio';
import { APIKey } from "../apis/apiKey";

const Login = ({ onLoginSuccess }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState(null);
    const navigate = useNavigate();

    const submitHandler = (event) => {
        const requestConfig = {
            headers: {
                'x-api-key': APIKey
            }
        };
        const requestBody = {
            username: username,
            password: password
        };

        // Login API call
        axios.post('/login', requestBody, requestConfig)
            .then((response) => {
                setUserSession(response.data.username, response.data.authToken, response.data.newUser);
                console.log(response);
                console.log("successful login");

                if (response.data.adminRight) {
                    // Redirect to AdminPage for admin users
                    console.log("Admin user detected, redirecting to AdminPage.");
                    navigate('/admin'); // Ensure there is a Route for AdminPage in your App.js
                } else {
                    // Call the onLoginSuccess prop to pass the response data back to App.js
                    onLoginSuccess(response.data);  // Update state in App.js
                }
            })
            .catch((error) => {
                console.log('Error:', error);
                setMessage(error.response?.data?.errorMessage?.split('] ')[1] || "Login failed.");
            });
    };

    return (
        <div>
            <div className="header">
                <Link to="/register">Register</Link>  {/* Link to register page */}
                <Link to="/login">Login</Link>  {/* Link to login page */}
            </div>
            <div id="alignpage">
                <h4>Login</h4>
                <div>
                    <label>
                        Username:
                        <input
                            className="field"
                            type="text"
                            value={username}
                            onChange={event => setUsername(event.target.value)}
                        />
                    </label>
                    <br />
                    <label>
                        Password:
                        <input
                            className="field"
                            type="password"
                            value={password}
                            onChange={event => setPassword(event.target.value)}
                        />
                    </label>
                    <br />
                </div>
            </div>
            <div id="outer">
                <input
                    className="inner"
                    type="button"
                    value="Login"
                    onClick={submitHandler}
                />
            </div>
            {message && <p className="message">{message}</p>}
        </div>
    );
};

export default Login;
