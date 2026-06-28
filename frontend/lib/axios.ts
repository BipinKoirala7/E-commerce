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

const fetcher = async (url: string) => {
  try {
    const res = await api.get(url);
    return res.data;
  } catch (error: any) {
    if (error?.response?.status === 401) return null;
    throw error;
  }
};

const healthApi = axios.create({
  baseURL: ApiEndpoint.BASE_URL,
  timeout: 10000,
  withCredentials: false,
});

const healthFetcher = async (url: string) => {
  const res = await healthApi.get(url);
  return res.data;
};

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

export { api, fetcher, healthFetcher };
