#!/bin/bash

# Create a new todo
# Usage: ./create-todo.sh "Todo Title" "Todo Description" [due_date]

BASE_URL="http://localhost:8080/api/todos"

if [ $# -lt 2 ]; then
    echo "Usage: $0 \"Todo Title\" \"Todo Description\" [due_date]"
    echo "Example: $0 \"Buy groceries\" \"Buy milk, bread, and eggs\" \"2025-09-21T15:00:00Z\""
    exit 1
fi

TITLE="$1"
DESCRIPTION="$2"
DUE_DATE="${3:-}"

if [ -n "$DUE_DATE" ]; then
    JSON_DATA=$(cat <<EOF
{
  "title": "$TITLE",
  "description": "$DESCRIPTION",
  "completed": false,
  "dueDate": "$DUE_DATE"
}
EOF
)
else
    JSON_DATA=$(cat <<EOF
{
  "title": "$TITLE",
  "description": "$DESCRIPTION",
  "completed": false
}
EOF
)
fi

echo "Creating todo with title: $TITLE"
echo "JSON Data: $JSON_DATA"
echo

response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d "$JSON_DATA")

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

echo "HTTP Status: $http_code"
echo "Response: $body"

if [ "$http_code" = "201" ]; then
    echo "✓ Todo created successfully!"
    todo_id=$(echo "$body" | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
    echo "Created Todo ID: $todo_id"
else
    echo "✗ Failed to create todo"
fi