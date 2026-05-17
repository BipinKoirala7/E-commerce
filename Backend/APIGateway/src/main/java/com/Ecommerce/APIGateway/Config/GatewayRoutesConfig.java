package com.Ecommerce.APIGateway.Config;

import com.Ecommerce.APIGateway.Security.SecurityUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Objects;

@Configuration
public class GatewayRoutesConfig {

  //  Help here: https://medium.com/att-israel/spring-cloud-gateway-mvc-migration-from-reactive-one-ed2025efc165

  @Value("${app.gateway.strip-prefix:0}")
  private int stripPrefixParts;

  @Value("${app.gateway.secret}")
  private String GATEWAY_SECRET;

  private final String GATEWAY_HEADER = "X-Gateway-Secret";
  private final String AUTH_HEADER = "Authorization";

  // Route
  private static final String USER_SERVICE = "user-service";
  private static final String PRODUCT_SERVICE = "product-service";
  private static final String ORDER_SERVICE = "order-service";
  private static final String CART_SERVICE = "cart-service";

  // Paths
  private static final String USER_PATH = "/user/**";
  private static final String AUTH_PATH = "/auth/**";
  private static final String OAUTH_LOGIN_PATH = "/login/oauth2/code/**";
  private static final String OAUTH_REDIRECT_PATH = "/oauth2/**";
  private static final String PRODUCT_PATH = "/product/**";
  private static final String CART_PATH = "/cart/**";
  private static final String CART_ITEM_PATH = "/cart-item/**";
  private static final String WISHLIST_PATH = "/wishlist/**";
  private static final String ORDER_PATH = "/order/**";
  private static final String PAYMENT_PATH = "/payment/**";

  @Bean
  public RouterFunction<ServerResponse> gatewayRouter() {
    return userServiceRoutes()
        .and(productServiceRoutes())
        .and(orderServiceRoutes())
        .and(cartServiceRoutes());
  }

  private @NonNull RouterFunction<ServerResponse> userServiceRoutes() {
    return gatewayRouter(USER_SERVICE, USER_PATH)
        .and(gatewayRouter(USER_SERVICE, AUTH_PATH))
        .and(gatewayRouter(USER_SERVICE, OAUTH_REDIRECT_PATH))
        .and(gatewayRouter(USER_SERVICE, OAUTH_LOGIN_PATH));
  }

  private @NonNull RouterFunction<ServerResponse> productServiceRoutes() {
    return GatewayRouterFunctions.route(PRODUCT_SERVICE)
        .before(BeforeFilterFunctions.stripPrefix(stripPrefixParts))
        .before(BeforeFilterFunctions.addRequestHeader(GATEWAY_HEADER, GATEWAY_SECRET))
        .route(GatewayRequestPredicates.path(PRODUCT_PATH), HandlerFunctions.http())
        .filter(LoadBalancerFilterFunctions.lb(PRODUCT_SERVICE))
        .build();
  }

  private @NonNull RouterFunction<ServerResponse> orderServiceRoutes() {
    return gatewayRouter(ORDER_SERVICE, ORDER_PATH)
        .and(gatewayRouter(ORDER_SERVICE, PAYMENT_PATH));
  }

  private @NonNull RouterFunction<ServerResponse> cartServiceRoutes() {
    return gatewayRouter(CART_SERVICE, CART_PATH)
        .and(gatewayRouter(CART_SERVICE, CART_ITEM_PATH))
        .and(gatewayRouter(CART_SERVICE, WISHLIST_PATH));
  }

  private @NonNull RouterFunction<ServerResponse> gatewayRouter(String serviceName, String servicePath) {
    return GatewayRouterFunctions.route(serviceName)
        .before(BeforeFilterFunctions.stripPrefix(stripPrefixParts))
        .before(BeforeFilterFunctions.addRequestHeader(GATEWAY_HEADER, GATEWAY_SECRET))
        .before(request -> {
          String token = SecurityUtils.getAccessToken();
          if (token != null && !token.isBlank()) {
            return BeforeFilterFunctions.addRequestHeader(AUTH_HEADER, authBearer(token)).apply(request);
          }
          return request;
        })
        .route(GatewayRequestPredicates.path(servicePath), HandlerFunctions.http())
        .filter(LoadBalancerFilterFunctions.lb(serviceName))
        .build();
  }

  private String authBearer(String token) {
    return Objects.requireNonNull(token).startsWith("Bearer ") ? token : "Bearer " + token;
  }
}