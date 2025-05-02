import axios from "axios";

const api = axios.create({
    baseURL: 'http://localhost:8080/api/'
});

api.interceptors.response.use(
    response => response.data,
    error => {
        console.error('Error on the requisition:', error);
        return Promise.reject(error);
    }
);

const UserAPI = {
    getAll: () => api.get('users')
}


export default {
    users: UserAPI
};