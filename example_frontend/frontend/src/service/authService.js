module.exports = {
    getUsername: function() {
        return sessionStorage.getItem('user');
    },

    getToken: function () {
        return sessionStorage.getItem('token');
    },

    isNewUser: function () {
        return sessionStorage.getItem('newUser');
    },

    setNewUser: function(newUser) {
        sessionStorage.setItem('newUser', newUser);
    },

    setUserSession: function (username, token, newUser) {
        sessionStorage.setItem('newUser', newUser);
        sessionStorage.setItem('user', username);
        sessionStorage.setItem('token', token);
    },

    resetUserSession: function () {
        sessionStorage.removeItem('user');
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('newUser');
    }
}