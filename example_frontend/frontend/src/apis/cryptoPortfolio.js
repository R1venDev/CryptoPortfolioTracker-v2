import axios from "axios";

// cryptoProjectUser
const instance = axios.create({
    baseURL: 'http://localhost/api'
});
export default instance;

