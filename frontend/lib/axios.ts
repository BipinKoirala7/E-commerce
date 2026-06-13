import axios from "axios";
import { ApiEndpoint } from "./ApiEndpoint";

const api = axios.create({
  baseURL: ApiEndpoint.BASE_URL,
  timeout: 10000,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json",
  },
});

const fetcher = (url: string) => api.get(url).then((res) => res.data);

api.interceptors.request.use((config) => {
  console.log("BASE URL:", config.baseURL);
  console.log("URL:", config.url);

  const fullUrl = `${config.baseURL}${config.url}`;

  console.log("FULL URL:", fullUrl);

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const { response, request } = error;

    if (response) {
      console.error("API Error:", response.status, response.data);
    } else if (request) {
      console.error("No response received:", request);
    } else {
      console.error("Error setting up request:", error.message);
    }
    return Promise.reject(error);
  },
);

export { api, fetcher };
