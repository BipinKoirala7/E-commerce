import {
  UserLoginResponse,
  LoginUserInfo,
  RegisterUserInfo,
  UserSignUpResponse,
  ApiResponse,
} from "@/types";
import { api } from "@/lib/axios";
import { ApiEndpoint } from "@/lib/ApiEndpoint";
import toast from "react-hot-toast";

async function handleEmailLogIn(userInfo: LoginUserInfo) {
  try {
    const response = await api.post<UserLoginResponse>(
      ApiEndpoint.LOGIN,
      userInfo,
    );

    if (response.data.success) {
      window.location.href = "/";
    } else {
      toast.error(response.data.message);
    }
  } catch (error) {
    console.error("Signup failed:", error);
    if (error instanceof Error) {
      toast.error(error.message);
    }
  }
}

async function handleEmailSignup(userInfo: RegisterUserInfo) {
  try {
    const response = await api.post<UserSignUpResponse>(
      ApiEndpoint.REGISTER,
      userInfo,
    );
    // redirect to login page
    if (response.data.success) {
      window.location.href = "/auth/login";
    } else {
      toast.error(response.data.message);
    }
  } catch (error) {
    console.error("Signup failed:", error);
    if (error instanceof Error) {
      toast.error(error.message);
    }
  }
}

function handleGoogleAuth() {
  window.location.href = ApiEndpoint.BASE_URL + ApiEndpoint.GOOGLE_LOGIN;
}

async function handleLogOut() {
  try {
    const response = await api.post<ApiResponse<void>>(ApiEndpoint.LOGOUT);

    if (response.data.success) {
      window.location.href = "/";
    } else {
      toast.error("Logging Out Failed");
    }
  } catch (error) {
    toast.error("Something went wrong");
    console.log("Error occured: ", error);
  }
}

export { handleGoogleAuth, handleEmailLogIn, handleEmailSignup, handleLogOut };
