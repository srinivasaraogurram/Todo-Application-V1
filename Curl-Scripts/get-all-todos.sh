#!/bin/bash

# Get all todos
# Usage: ./get-all-todos.sh

BASE_URL="http://localhost:8080/api/todos"

echo "Fetching all todos..."
echo

response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL")

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

echo "HTTP Status: $http_code"

if [ "$http_code" = "200" ]; then
    echo "✓ Successfully retrieved todos"
    echo
    echo "Todos:"
    echo "$body" | jq '.' 2>/dev/null || echo "$body"
else
    echo "✗ Failed to retrieve todos"
    echo "Response: $body"
fi