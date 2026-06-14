export enum ApiEndpoint {
  BASE_URL = "http://localhost:4000/api",

  // Auth Actions
  LOGIN = "/auth/login",
  REGISTER = "/auth/register",
  LOGOUT = "/auth/logout",
  GOOGLE_LOGIN = "/oauth2/authorization/google",

  // Resource Routes
  USER = "/user", // logged in user
  CART = "/cart-item",
  ORDER = "/order",
  PRODUCT = "/product",
  PAYMENT = "/payment",
}
