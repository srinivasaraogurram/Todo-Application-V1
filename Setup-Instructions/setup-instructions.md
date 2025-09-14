# Todo Application Setup Instructions

## Prerequisites

Before running the Todo Application, ensure you have the following installed:

- **Java 21** or higher
- **Node.js 18** or higher
- **npm** (comes with Node.js)
- **Maven 3.8** or higher
- **Git** (for version control)

## Project Structure

```
Todo-Application-V1/
├── Back-END/
│   └── todo-api/               # Spring Boot backend application
├── Front-END/
│   └── todo-app/               # React frontend application
├── Database/
│   ├── schema.sql              # Database schema
│   └── data.sql                # Seed data
├── Swagger/
│   └── api-documentation.md    # API documentation
├── Postman-Scripts/
│   ├── Todo-Application-API.postman_collection.json
│   └── Todo-Application-Environment.postman_environment.json
├── Curl-Scripts/
│   ├── test-api.sh            # Complete API test script
│   ├── create-todo.sh         # Create todo script
│   ├── get-all-todos.sh       # Get all todos script
│   ├── update-todo.sh         # Update todo script
│   ├── delete-todo.sh         # Delete todo script
│   └── toggle-todo.sh         # Toggle todo status script
├── Integration-TestCases/
│   └── (Cucumber integration tests)
└── Setup-Instructions/
    ├── setup-instructions.md  # This file
    └── about-todo-application.md
```

## Setup Instructions

### 1. Backend Setup (Spring Boot)

#### Step 1: Navigate to Backend Directory
```bash
cd Back-END/todo-api
```

#### Step 2: Install Dependencies
```bash
mvn clean install
```

#### Step 3: Run Backend Application
```bash
mvn spring-boot:run
```

**Alternative:** You can also run using Java directly:
```bash
mvn clean package
java -jar target/todo-api-0.0.1-SNAPSHOT.jar
```

The backend will start on **http://localhost:8080**

#### Backend Features:
- REST API endpoints for CRUD operations
- H2 in-memory database with seed data
- Swagger UI documentation at `http://localhost:8080/swagger-ui.html`
- H2 Console at `http://localhost:8080/h2-console` (username: sa, password: password)

### 2. Frontend Setup (React)

#### Step 1: Navigate to Frontend Directory
```bash
cd Front-END/todo-app
```

#### Step 2: Install Dependencies
```bash
npm install --legacy-peer-deps
```

#### Step 3: Run Frontend Application
```bash
npm start
```

The frontend will start on **http://localhost:3000**

#### Frontend Features:
- Modern React 18 application with TypeScript
- Material-UI components for professional styling
- Real-time todo management interface
- Search and filter functionality

### 3. Application Startup Order

**IMPORTANT:** Follow this exact order for proper functionality:

1. **Start Backend First** (Port 8080)
   ```bash
   cd Back-END/todo-api
   mvn spring-boot:run
   ```

2. **Wait for Backend to Fully Start** (Look for "Started TodoApiApplication" message)

3. **Start Frontend** (Port 3000)
   ```bash
   cd Front-END/todo-app
   npm start
   ```

### 4. Accessing the Applications

#### Frontend Application
- URL: http://localhost:3000
- Features: Create, read, update, delete todos with a modern UI

#### Backend API
- Base URL: http://localhost:8080/api/todos
- Swagger Documentation: http://localhost:8080/swagger-ui.html
- H2 Database Console: http://localhost:8080/h2-console

#### Database Access
- URL: jdbc:h2:mem:tododb
- Username: sa
- Password: password

## Testing the Application

### 1. Using the Web Interface
1. Open http://localhost:3000
2. Use the "Add Todo" button to create new todos
3. Click checkboxes to mark todos as complete
4. Use edit and delete buttons for todo management
5. Use search and filter options

### 2. Using Swagger UI
1. Open http://localhost:8080/swagger-ui.html
2. Explore and test all API endpoints interactively
3. View request/response schemas and examples

### 3. Using Postman
1. Import `Postman-Scripts/Todo-Application-API.postman_collection.json`
2. Import `Postman-Scripts/Todo-Application-Environment.postman_environment.json`
3. Run the collection to test all endpoints

### 4. Using cURL Scripts
```bash
# Make scripts executable (if not already)
chmod +x Curl-Scripts/*.sh

# Test all endpoints
./Curl-Scripts/test-api.sh

# Individual operations
./Curl-Scripts/create-todo.sh "Buy groceries" "Buy milk and bread"
./Curl-Scripts/get-all-todos.sh
./Curl-Scripts/update-todo.sh 1 "Updated title" "Updated description" true
./Curl-Scripts/toggle-todo.sh 1
./Curl-Scripts/delete-todo.sh 1
```

## Running Tests

### Backend Unit Tests
```bash
cd Back-END/todo-api
mvn test
```

### Frontend Unit Tests
```bash
cd Front-END/todo-app
npm test
```

### Integration Tests (Cucumber)
```bash
cd Integration-TestCases
mvn test
```

## Troubleshooting

### Common Issues

#### 1. Port Already in Use
- **Backend (8080):** Kill process using `lsof -ti:8080 | xargs kill -9`
- **Frontend (3000):** Kill process using `lsof -ti:3000 | xargs kill -9`

#### 2. CORS Issues
- Ensure backend is running before frontend
- Check that CORS is configured for `http://localhost:3000`

#### 3. Database Issues
- H2 database is in-memory and resets on restart
- Seed data is automatically loaded on startup

#### 4. Maven Dependencies
```bash
cd Back-END/todo-api
mvn clean install -U
```

#### 5. npm Dependencies
```bash
cd Front-END/todo-app
rm -rf node_modules package-lock.json
npm install --legacy-peer-deps
```

## Default Seed Data

The application comes with 10 sample todos covering various scenarios:
- Completed and pending tasks
- Tasks with due dates
- Tasks with detailed descriptions

## Development Mode

### Hot Reload
- **Backend:** Uses Spring Boot DevTools for automatic restart
- **Frontend:** React development server provides hot reload

### Database Persistence
- **Development:** H2 in-memory database (data lost on restart)
- **Production:** Can be configured for persistent databases (PostgreSQL, MySQL)

## Production Deployment

### Backend
```bash
mvn clean package
java -jar target/todo-api-0.0.1-SNAPSHOT.jar --server.port=8080
```

### Frontend
```bash
npm run build
# Serve the build folder with a static file server
```

## Additional Resources

- **API Documentation:** `Swagger/api-documentation.md`
- **Application Details:** `Setup-Instructions/about-todo-application.md`
- **Postman Collection:** `Postman-Scripts/`
- **Test Scripts:** `Curl-Scripts/`

## Support

If you encounter any issues:
1. Check the troubleshooting section above
2. Verify all prerequisites are installed
3. Ensure the startup order is followed correctly
4. Check application logs for error messages