#!/bin/bash

# Toggle todo completion status
# Usage: ./toggle-todo.sh todo_id

BASE_URL="http://localhost:8080/api/todos"

if [ $# -lt 1 ]; then
    echo "Usage: $0 todo_id"
    echo "Example: $0 1"
    exit 1
fi

TODO_ID="$1"

echo "Toggling completion status for todo ID: $TODO_ID"
echo

response=$(curl -s -w "\n%{http_code}" -X PATCH "$BASE_URL/$TODO_ID/toggle")

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

echo "HTTP Status: $http_code"

if [ "$http_code" = "200" ]; then
    echo "✓ Todo status toggled successfully!"
    echo "Updated Todo:"
    echo "$body" | jq '.' 2>/dev/null || echo "$body"
elif [ "$http_code" = "404" ]; then
    echo "✗ Todo with ID $TODO_ID not found"
else
    echo "✗ Failed to toggle todo status"
    echo "Response: $body"
fi