import {
  UserLoginResponse,
  LoginUserInfo,
  RegisterUserInfo,
  UserSignUpResponse,
} from "@/types";
import { api } from "@/lib/axios";
import { ApiEndpoint } from "../ApiEndpoint";

async function handleEmailSignup(userInfo: RegisterUserInfo) {
  try {
    const response = await api.post<UserSignUpResponse>(
      ApiEndpoint.REGISTER,
      userInfo,
    );
    // redirect to login page
    if (response.data.success) {
      window.location.href = "/auth/login";
    }
  } catch (error) {
    console.error("Signup failed:", error);
  }
}

function handleGoogleAuth() {
  window.location.href = ApiEndpoint.BASE_URL + ApiEndpoint.GOOGLE_LOGIN;
}

async function handleEmailLogin(userInfo: LoginUserInfo) {
  try {
    const response = await api.post<UserLoginResponse>(
      ApiEndpoint.LOGIN,
      userInfo,
    );
    if (response.data.success) {
      window.location.href = "/";
    }
  } catch (error) {
    console.error("Login failed:", error);
  }
}

export { handleGoogleAuth, handleEmailLogin, handleEmailSignup };
