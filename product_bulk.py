#!/usr/bin/env python3

import json
import urllib.request
import urllib.error
import time

API_URL = "http://localhost:8080/api/v1/product"
DATA_FILE = "products_data.json"

with open(DATA_FILE, "r") as f:
    products = json.load(f)

total = len(products)
success = 0
failed = 0

print("========================================")
print(f" Sending {total} product(s) to {API_URL}")
print("========================================")

for i, product in enumerate(products, 1):
    name = product.get("name", f"Product {i}")
    print(f"\n[{i}/{total}] Sending: {name}")

    body = json.dumps(product).encode("utf-8")
    req = urllib.request.Request(
        API_URL,
        data=body,
        headers={"Content-Type": "application/json"},
        method="POST"
    )

    try:
        with urllib.request.urlopen(req) as res:
            status = res.status
            response_body = res.read().decode("utf-8")
            print(f"  ✅ Success ({status}): {response_body}")
            success += 1
    except urllib.error.HTTPError as e:
        error_body = e.read().decode("utf-8")
        print(f"  ❌ Failed ({e.code}): {error_body}")
        failed += 1
    except urllib.error.URLError as e:
        print(f"  ❌ Connection error: {e.reason}")
        failed += 1

    time.sleep(0.1)

print("\n========================================")
print(f" Done! ✅ {success} succeeded  ❌ {failed} failed")
print("========================================")
