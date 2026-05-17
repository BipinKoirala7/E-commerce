// User enums, interfaces, and types
export enum Role {
  USER,
  ADMIN,
}

export interface User {
  userName: string;
  email: string;
  emailVerified: boolean;
  role: Role;
  profilePictureUrl: string;
  lastLoginAt: Date;
  createdAt: Date;
  updatedAt: Date;
}

export type RegisterUserInfo = Omit<
  User,
  | "emailVerified"
  | "role"
  | "profilePictureUrl"
  | "lastLoginAt"
  | "createdAt"
  | "updatedAt"
> & {
  userName: string;
  email: string;
  password: string;
};

export type LoginUserInfo = Omit<RegisterUserInfo, "userName">;

// Product enums, interfaces, and types

export const Category = {
  All: "all",
  Men: "men",
  Women: "women",
} as const;

export type Category = (typeof Category)[keyof typeof Category];

export type ProductSearchParams = {
  query?: string;
  category?: string;
  minPrice?: string;
  maxPrice?: string;
  sort?: string;
  page?: string;
  size?: string;
  direction: "ASC" | "DESC";
};

export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  imageUrl: string;
  brand: string;
  weight: number;
  weightUnit: string;
  createdAt: Date;
  updatedAt: Date;
}

export type ProductSummary = Omit<
  Product,
  | "description"
  | "stockQuantity"
  | "weight"
  | "weightUnit"
  | "createdAt"
  | "updatedAt"
>;

// Reverted Back to previous ProductSummary
export type ProductSearchResult = PageableResult<ProductSummary>;

// Wishlist enums, interfaces, and types

export interface WishList {
  id: string;
  createdAt: Date;
}

export type AddToWishlist = {
  productId: string;
};

export type WishListItem = Omit<WishList, "createdAt"> & {
  product: ProductSummary;
};

// Cart enums, interfaces, and types
export interface CartItem {
  id: string;
  productId: string;
  quantity: number;
  createdAt: Date;
  updatedAt: Date;
}

export type AddCartItem = {
  productId: string;
};

export type CartProductSummary = Omit<CartItem, "productId"> & {
  product: ProductSummary;
};

// Order enums, interfaces, and types
export enum OrderStatus {
  PENDING = "PENDING",
  CONFIRMED = "CONFIRMED",
  PROCESSING = "PROCESSING",
  DELIVERED = "DELIVERED",
  CANCELLED = "CANCELLED",
  RETURNED = "RETURNED",
}

export interface OrderItem {
  id: string;
  productId: string;
  quantity: number;
}

export interface Order {
  id: string;
  billingAddress: string;
  shippingAddress: string;
  email: string;
  phone: string;
  orderItems: OrderItem[];
  totalPrice: number;
  orderStatus: OrderStatus;
  createdAt: Date;
  updatedAt: Date;
}

export type OrderSummary = Omit<
  Order,
  | "billingAddress"
  | "shippingAddress"
  | "email"
  | "phone"
  | "orderItems"
  | "updatedAt"
>;
// Response enums, interfaces, and types
export type ApiResponse<T> = {
  statusCode: number;
  success: boolean;
  data: T;
  message: string;
  timestamp: string;
};

// Common enums, interfaces, and types

export const SortDirection = {
  Ascending: "ASC",
  Descending: "DESC",
} as const;

export type SortDirection = (typeof SortDirection)[keyof typeof SortDirection];

export type PageableResult<T> = {
  content: T[];
  currentPage: number;
  totalPages: number;
  totalElements: number;
  isFirst: boolean;
  isLast: boolean;
  pageSize: number;
  numberOfElements: number;
};

export type UserLoginResponse = ApiResponse<boolean>;
export type UserSignUpResponse = ApiResponse<boolean>;
export type UserResponse = ApiResponse<User>;
export type ProductSearchResponse = ApiResponse<ProductSearchResult>;
export type ProductDetailsResponse = ApiResponse<Product>;
export type WishListResponse = ApiResponse<WishListItem[]>;
export type AddToWishlistResponse = ApiResponse<void>;
export type RemoveFromWishlistResponse = ApiResponse<void>;
export type CartResponse = ApiResponse<CartProductSummary[]>;
export type AddToCartResponse = ApiResponse<void>;
export type RemoveFromCartResponse = ApiResponse<void>;
