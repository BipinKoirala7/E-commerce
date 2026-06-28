// lib/boot-sequence.ts
import { ApiEndpoint } from "@/lib/ApiEndpoint"; // adjust path to your actual file
import { ServerKey } from "@/store/zustand";

export const BOOT_SEQUENCE: { key: ServerKey; label: string; url: string }[] = [
  {
    key: "configServer",
    label: "Config Server",
    url: ApiEndpoint.CONFIG_SERVER_ACTUATOR_CHECK,
  },
  {
    key: "eurekaServer",
    label: "Eureka Server",
    url: ApiEndpoint.EUREKA_SERVER_ACTUATOR_CHECK,
  },
  {
    key: "apiGateway",
    label: "API Gateway",
    url: ApiEndpoint.API_GATEWAY_ACTUATOR_CHECK,
  },
  {
    key: "userService",
    label: "User Service",
    url: ApiEndpoint.USER_SERVICE_ACTUATOR_CHECK,
  },
  {
    key: "orderService",
    label: "Order Service",
    url: ApiEndpoint.ORDER_SERVICE_ACTUATOR_CHECK,
  },
  {
    key: "productService",
    label: "Product Service",
    url: ApiEndpoint.PRODUCT_SERVICE_ACTUATOR_CHECK,
  },
];

export const MAX_ATTEMPTS = 12; // e.g. 12 × 5s = 1 minute before giving up
export const RETRY_INTERVAL_MS = 5000;
