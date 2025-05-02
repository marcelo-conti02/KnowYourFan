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

const FanProfileAPI = {
    getAll: () => api.get('fan-profiles'),
    getById: (id) => api.get(`fan-profiles/user/${id}`)
}


export default {
    users: UserAPI,
    fanProfiles: FanProfileAPI
};