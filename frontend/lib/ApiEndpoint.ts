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

  // Server Info
  CONFIG_SERVER_ACTUATOR_CHECK:
    process.env.NEXT_PUBLIC_BASE_CONFIG_SERVER_URL! + "/actuator/health",
  EUREKA_SERVER_ACTUATOR_CHECK:
    process.env.NEXT_PUBLIC_BASE_EUREKA_SERVER_URL! + "/actuator/health",
  API_GATEWAY_ACTUATOR_CHECK:
    process.env.NEXT_PUBLIC_BASE_API_GATEWAY_URL! + "/actuator/health",
  USER_SERVICE_ACTUATOR_CHECK:
    process.env.NEXT_PUBLIC_BASE_USER_SERVICE_URL! + "/actuator/health",
  PRODUCT_SERVICE_ACTUATOR_CHECK:
    process.env.NEXT_PUBLIC_BASE_PRODUCT_SERVICE_URL! + "/actuator/health",
  ORDER_SERVICE_ACTUATOR_CHECK:
    process.env.NEXT_PUBLIC_BASE_ORDER_SERVICE_URL! + "/actuator/health",
} as const;
export type ApiEndpointType = typeof ApiEndpoint;
