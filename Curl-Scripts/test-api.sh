#!/bin/bash

# Todo Application API Testing with cURL
# This script demonstrates how to test all the Todo API endpoints using cURL

BASE_URL="http://localhost:8080/api/todos"

echo "======================================"
echo "Todo Application API Testing with cURL"
echo "======================================"
echo

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ SUCCESS${NC}"
    else
        echo -e "${RED}✗ FAILED${NC}"
    fi
}

# Test 1: Get all todos
echo -e "${YELLOW}1. Testing GET /api/todos - Get all todos${NC}"
response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

echo "HTTP Status: $http_code"
echo "Response: $body"
if [ "$http_code" = "200" ]; then
    print_status 0
else
    print_status 1
fi
echo

# Test 2: Create a new todo
echo -e "${YELLOW}2. Testing POST /api/todos - Create new todo${NC}"
create_response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Todo from cURL",
    "description": "This todo was created using cURL script",
    "completed": false,
    "dueDate": "2025-09-21T15:00:00Z"
  }')

http_code=$(echo "$create_response" | tail -n1)
body=$(echo "$create_response" | head -n -1)
echo "HTTP Status: $http_code"
echo "Response: $body"

if [ "$http_code" = "201" ]; then
    # Extract the ID of the created todo
    todo_id=$(echo "$body" | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
    echo "Created Todo ID: $todo_id"
    print_status 0
else
    print_status 1
    todo_id=""
fi
echo

# Test 3: Get todo by ID (only if we have a valid ID)
if [ -n "$todo_id" ]; then
    echo -e "${YELLOW}3. Testing GET /api/todos/{id} - Get todo by ID${NC}"
    response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/$todo_id")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    echo "HTTP Status: $http_code"
    echo "Response: $body"
    if [ "$http_code" = "200" ]; then
        print_status 0
    else
        print_status 1
    fi
    echo
fi

# Test 4: Update todo (only if we have a valid ID)
if [ -n "$todo_id" ]; then
    echo -e "${YELLOW}4. Testing PUT /api/todos/{id} - Update todo${NC}"
    response=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/$todo_id" \
      -H "Content-Type: application/json" \
      -d '{
        "title": "Updated Test Todo from cURL",
        "description": "This todo was updated using cURL script",
        "completed": true,
        "dueDate": "2025-09-22T15:00:00Z"
      }')
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    echo "HTTP Status: $http_code"
    echo "Response: $body"
    if [ "$http_code" = "200" ]; then
        print_status 0
    else
        print_status 1
    fi
    echo
fi

# Test 5: Toggle todo status (only if we have a valid ID)
if [ -n "$todo_id" ]; then
    echo -e "${YELLOW}5. Testing PATCH /api/todos/{id}/toggle - Toggle todo status${NC}"
    response=$(curl -s -w "\n%{http_code}" -X PATCH "$BASE_URL/$todo_id/toggle")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    echo "HTTP Status: $http_code"
    echo "Response: $body"
    if [ "$http_code" = "200" ]; then
        print_status 0
    else
        print_status 1
    fi
    echo
fi

# Test 6: Get todos by status - completed
echo -e "${YELLOW}6. Testing GET /api/todos/status?completed=true - Get completed todos${NC}"
response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/status?completed=true")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

echo "HTTP Status: $http_code"
echo "Response: $body"
if [ "$http_code" = "200" ]; then
    print_status 0
else
    print_status 1
fi
echo

# Test 7: Get todos by status - active
echo -e "${YELLOW}7. Testing GET /api/todos/status?completed=false - Get active todos${NC}"
response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/status?completed=false")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

echo "HTTP Status: $http_code"
echo "Response: $body"
if [ "$http_code" = "200" ]; then
    print_status 0
else
    print_status 1
fi
echo

# Test 8: Search todos
echo -e "${YELLOW}8. Testing GET /api/todos/search?title=project - Search todos${NC}"
response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/search?title=project")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

echo "HTTP Status: $http_code"
echo "Response: $body"
if [ "$http_code" = "200" ]; then
    print_status 0
else
    print_status 1
fi
echo

# Test 9: Delete todo (only if we have a valid ID)
if [ -n "$todo_id" ]; then
    echo -e "${YELLOW}9. Testing DELETE /api/todos/{id} - Delete todo${NC}"
    response=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/$todo_id")
    http_code=$(echo "$response" | tail -n1)
    
    echo "HTTP Status: $http_code"
    if [ "$http_code" = "204" ]; then
        print_status 0
        echo "Todo successfully deleted"
    else
        print_status 1
    fi
    echo
fi

# Test 10: Verify deleted todo returns 404
if [ -n "$todo_id" ]; then
    echo -e "${YELLOW}10. Testing GET /api/todos/{id} - Verify deleted todo returns 404${NC}"
    response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/$todo_id")
    http_code=$(echo "$response" | tail -n1)
    
    echo "HTTP Status: $http_code"
    if [ "$http_code" = "404" ]; then
        print_status 0
        echo "Correctly returns 404 for deleted todo"
    else
        print_status 1
    fi
    echo
fi

echo "======================================"
echo "API Testing Complete!"
echo "======================================" 