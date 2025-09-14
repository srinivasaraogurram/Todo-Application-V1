#!/bin/bash

# Update a todo
# Usage: ./update-todo.sh todo_id "New Title" "New Description" [true|false] [due_date]

BASE_URL="http://localhost:8080/api/todos"

if [ $# -lt 3 ]; then
    echo "Usage: $0 todo_id \"New Title\" \"New Description\" [true|false] [due_date]"
    echo "Example: $0 1 \"Updated Todo\" \"Updated description\" true \"2025-09-22T15:00:00Z\""
    exit 1
fi

TODO_ID="$1"
TITLE="$2"
DESCRIPTION="$3"
COMPLETED="${4:-false}"
DUE_DATE="${5:-}"

if [ -n "$DUE_DATE" ]; then
    JSON_DATA=$(cat <<EOF
{
  "title": "$TITLE",
  "description": "$DESCRIPTION",
  "completed": $COMPLETED,
  "dueDate": "$DUE_DATE"
}
EOF
)
else
    JSON_DATA=$(cat <<EOF
{
  "title": "$TITLE",
  "description": "$DESCRIPTION",
  "completed": $COMPLETED
}
EOF
)
fi

echo "Updating todo ID: $TODO_ID"
echo "JSON Data: $JSON_DATA"
echo

response=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/$TODO_ID" \
  -H "Content-Type: application/json" \
  -d "$JSON_DATA")

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

echo "HTTP Status: $http_code"

if [ "$http_code" = "200" ]; then
    echo "✓ Todo updated successfully!"
    echo "Updated Todo:"
    echo "$body" | jq '.' 2>/dev/null || echo "$body"
elif [ "$http_code" = "404" ]; then
    echo "✗ Todo with ID $TODO_ID not found"
else
    echo "✗ Failed to update todo"
    echo "Response: $body"
fi