export const ApiEndpoint = {
  BASE_URL: process.env.NEXT_PUBLIC_BASE_BACKEND_URL!,

  // Auth Actions
  LOGIN: process.env.NEXT_PUBLIC_BASE_USER_EMAIL_LOGIN_URL!,
  REGISTER: process.env.NEXT_PUBLIC_BASE_USER_EMAIL_REGISTER_URL!,
  LOGOUT: process.env.NEXT_PUBLIC_BASE_USER_LOGOUT_URL!,
  GOOGLE_LOGIN: process.env.NEXT_PUBLIC_BASE_USER_GOOGLE_LOGIN_URL!,

  // Resource Routes
  USER: process.env.NEXT_PUBLIC_BASE_USER_URL!,
  CART: process.env.NEXT_PUBLIC_BASE_CART_URL!,
  ORDER: process.env.NEXT_PUBLIC_BASE_ORDER_URL!,
  PRODUCT: process.env.NEXT_PUBLIC_BASE_PRODUCT_URL!,
  PAYMENT: process.env.NEXT_PUBLIC_BASE_PAYMENT_URL!,
} as const;
export type ApiEndpointType = typeof ApiEndpoint;
