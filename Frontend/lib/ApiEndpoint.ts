export enum ApiEndpoint {
  BASE_URL = "http://localhost:4000/api",

  // Auth Actions
  LOGIN = "/auth/login",
  REGISTER = "/auth/register",
  LOGOUT = "/auth/logout",
  GOOGLE_LOGIN = "/oauth2/authorization/google",

  // Resource Routes
  GET_USER = "/user", // logged in user
  GET_CART = "/cart-item",
  GET_WISHLIST = "/wishlist",
  GET_ORDER = "/order",
  GET_PRODUCT = "/product",
}
