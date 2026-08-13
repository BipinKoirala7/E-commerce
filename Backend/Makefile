DEBUG_COMPOSE_FILE := docker-compose.debug.yml

.PHONY: debug-user-service debug-order-service debug-product-service debug-api-gateway \
        debug-down debug-logs-user-service debug-logs-order-service help

debug-user-service:
	docker compose -f $(DEBUG_COMPOSE_FILE) up --build user-service

debug-order-service:
	docker compose -f $(DEBUG_COMPOSE_FILE) up --build order-service

debug-product-service:
	docker compose -f $(DEBUG_COMPOSE_FILE) up --build product-service

debug-api-gateway:
	docker compose -f $(DEBUG_COMPOSE_FILE) up --build api-gateway

debug-logs-user-service:
	docker compose -f $(DEBUG_COMPOSE_FILE) logs -f user-service

debug-logs-order-service:
	docker compose -f $(DEBUG_COMPOSE_FILE) logs -f order-service

debug-down:
	docker compose -f $(DEBUG_COMPOSE_FILE) down

help:
	@echo "Debug targets:"
	@echo "  make debug-user-service       - build+run user-service tests, suspend on port 5006"
	@echo "  make debug-order-service      - build+run order-service tests, suspend on port 5008"
	@echo "  make debug-product-service    - (uncomment service first) suspend on port 5007"
	@echo "  make debug-api-gateway        - (uncomment service first) suspend on port 5005"
	@echo "  make debug-logs-user-service  - tail logs for user-service debug container"
	@echo "  make debug-logs-order-service - tail logs for order-service debug container"
	@echo "  make debug-down               - stop and remove all debug containers"