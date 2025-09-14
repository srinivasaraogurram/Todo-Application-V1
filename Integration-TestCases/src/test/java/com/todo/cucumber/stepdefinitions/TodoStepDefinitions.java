package com.todo.cucumber.stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TodoStepDefinitions {

    private Response response;
    private String todoJson;
    private Long createdTodoId;
    private String baseUrl = "http://localhost:8080/api/todos";

    @Given("the todo application is running")
    public void theTodoApplicationIsRunning() {
        RestAssured.baseURI = "http://localhost:8080";
        // Verify the application is running by hitting the health endpoint
        given()
            .when()
                .get("/api/todos")
            .then()
                .statusCode(anyOf(is(200), is(404))); // Accept both, as the list might be empty
    }

    @Given("I have a new todo with title {string} and description {string}")
    public void iHaveANewTodoWithTitleAndDescription(String title, String description) {
        todoJson = String.format(
            "{\"title\":\"%s\",\"description\":\"%s\",\"completed\":false}",
            title, description
        );
    }

    @When("I create the todo")
    public void iCreateTheTodo() {
        response = given()
            .contentType(ContentType.JSON)
            .body(todoJson)
        .when()
            .post(baseUrl)
        .then()
            .extract().response();
        
        if (response.getStatusCode() == 201) {
            createdTodoId = response.jsonPath().getLong("id");
        }
    }

    @Then("the todo should be created successfully")
    public void theTodoShouldBeCreatedSuccessfully() {
        Assertions.assertEquals(201, response.getStatusCode());
    }

    @Then("the todo should have id greater than {int}")
    public void theTodoShouldHaveIdGreaterThan(int expectedMinId) {
        Long actualId = response.jsonPath().getLong("id");
        Assertions.assertTrue(actualId > expectedMinId);
    }

    @Then("the todo should be marked as not completed")
    public void theTodoShouldBeMarkedAsNotCompleted() {
        Boolean completed = response.jsonPath().getBoolean("completed");
        Assertions.assertFalse(completed);
    }

    @Given("I have created multiple todos:")
    public void iHaveCreatedMultipleTodos(DataTable dataTable) {
        List<Map<String, String>> todos = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> todo : todos) {
            String json = String.format(
                "{\"title\":\"%s\",\"description\":\"%s\",\"completed\":%s}",
                todo.get("title"),
                todo.get("description"),
                todo.get("completed")
            );
            
            given()
                .contentType(ContentType.JSON)
                .body(json)
            .when()
                .post(baseUrl)
            .then()
                .statusCode(201);
        }
    }

    @When("I retrieve all todos")
    public void iRetrieveAllTodos() {
        response = given()
        .when()
            .get(baseUrl)
        .then()
            .extract().response();
    }

    @Then("I should get {int} todos")
    public void iShouldGetTodos(int expectedCount) {
        List<Object> todos = response.jsonPath().getList("$");
        Assertions.assertEquals(expectedCount, todos.size());
    }

    @Then("the todos should contain {string}")
    public void theTodosShouldContain(String expectedTitle) {
        List<String> titles = response.jsonPath().getList("title");
        Assertions.assertTrue(titles.contains(expectedTitle));
    }

    @Given("I have a todo with title {string}")
    public void iHaveATodoWithTitle(String title) {
        todoJson = String.format(
            "{\"title\":\"%s\",\"description\":\"Test description\",\"completed\":false}",
            title
        );
        
        Response createResponse = given()
            .contentType(ContentType.JSON)
            .body(todoJson)
        .when()
            .post(baseUrl)
        .then()
            .statusCode(201)
            .extract().response();
        
        createdTodoId = createResponse.jsonPath().getLong("id");
    }

    @When("I update the todo title to {string}")
    public void iUpdateTheTodoTitleTo(String newTitle) {
        String updateJson = String.format(
            "{\"title\":\"%s\",\"description\":\"Test description\",\"completed\":false}",
            newTitle
        );
        
        response = given()
            .contentType(ContentType.JSON)
            .body(updateJson)
        .when()
            .put(baseUrl + "/" + createdTodoId)
        .then()
            .extract().response();
    }

    @Then("the todo should have the updated title {string}")
    public void theTodoShouldHaveTheUpdatedTitle(String expectedTitle) {
        Assertions.assertEquals(200, response.getStatusCode());
        String actualTitle = response.jsonPath().getString("title");
        Assertions.assertEquals(expectedTitle, actualTitle);
    }

    @When("I delete the todo")
    public void iDeleteTheTodo() {
        response = given()
        .when()
            .delete(baseUrl + "/" + createdTodoId)
        .then()
            .extract().response();
    }

    @Then("the todo should be removed from the system")
    public void theTodoShouldBeRemovedFromTheSystem() {
        Assertions.assertEquals(204, response.getStatusCode());
        
        // Verify the todo is actually deleted
        given()
        .when()
            .get(baseUrl + "/" + createdTodoId)
        .then()
            .statusCode(404);
    }

    @Given("I have a todo with title {string} that is not completed")
    public void iHaveATodoWithTitleThatIsNotCompleted(String title) {
        iHaveATodoWithTitle(title);
    }

    @When("I toggle the completion status")
    public void iToggleTheCompletionStatus() {
        response = given()
        .when()
            .patch(baseUrl + "/" + createdTodoId + "/toggle")
        .then()
            .extract().response();
    }

    @Then("the todo should be marked as completed")
    public void theTodoShouldBeMarkedAsCompleted() {
        Boolean completed = response.jsonPath().getBoolean("completed");
        Assertions.assertTrue(completed);
    }

    @When("I toggle the completion status again")
    public void iToggleTheCompletionStatusAgain() {
        iToggleTheCompletionStatus();
    }

    @Given("I have created todos with different completion status:")
    public void iHaveCreatedTodosWithDifferentCompletionStatus(DataTable dataTable) {
        List<Map<String, String>> todos = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> todo : todos) {
            String json = String.format(
                "{\"title\":\"%s\",\"description\":\"Test description\",\"completed\":%s}",
                todo.get("title"),
                todo.get("completed")
            );
            
            given()
                .contentType(ContentType.JSON)
                .body(json)
            .when()
                .post(baseUrl)
            .then()
                .statusCode(201);
        }
    }

    @When("I filter todos by completion status {string}")
    public void iFilterTodosByCompletionStatus(String completed) {
        response = given()
            .queryParam("completed", completed)
        .when()
            .get(baseUrl + "/status")
        .then()
            .extract().response();
    }

    @Then("all returned todos should be not completed")
    public void allReturnedTodosShouldBeNotCompleted() {
        List<Boolean> completedStatuses = response.jsonPath().getList("completed");
        for (Boolean status : completedStatuses) {
            Assertions.assertFalse(status);
        }
    }

    @Given("I have created todos:")
    public void iHaveCreatedTodos(DataTable dataTable) {
        List<Map<String, String>> todos = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> todo : todos) {
            String json = String.format(
                "{\"title\":\"%s\",\"description\":\"Test description\",\"completed\":false}",
                todo.get("title")
            );
            
            given()
                .contentType(ContentType.JSON)
                .body(json)
            .when()
                .post(baseUrl)
            .then()
                .statusCode(201);
        }
    }

    @When("I search for todos with title containing {string}")
    public void iSearchForTodosWithTitleContaining(String searchTerm) {
        response = given()
            .queryParam("title", searchTerm)
        .when()
            .get(baseUrl + "/search")
        .then()
            .extract().response();
    }

    @Then("all returned todos should have {string} in their title")
    public void allReturnedTodosShouldHaveInTheirTitle(String expectedSubstring) {
        List<String> titles = response.jsonPath().getList("title");
        for (String title : titles) {
            Assertions.assertTrue(title.contains(expectedSubstring));
        }
    }
}