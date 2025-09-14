Feature: Todo Management
  As a user
  I want to manage my todos
  So that I can organize my tasks effectively

  Background:
    Given the todo application is running

  Scenario: Create a new todo
    Given I have a new todo with title "Learn Cucumber" and description "Learn BDD testing with Cucumber"
    When I create the todo
    Then the todo should be created successfully
    And the todo should have id greater than 0
    And the todo should be marked as not completed

  Scenario: Retrieve all todos
    Given I have created multiple todos:
      | title           | description                    | completed |
      | Buy groceries   | Buy milk, bread, and eggs     | false     |
      | Finish project  | Complete the web application  | true      |
      | Call dentist    | Schedule dental appointment   | false     |
    When I retrieve all todos
    Then I should get 3 todos
    And the todos should contain "Buy groceries"

  Scenario: Update an existing todo
    Given I have a todo with title "Original Title"
    When I update the todo title to "Updated Title"
    Then the todo should have the updated title "Updated Title"

  Scenario: Delete a todo
    Given I have a todo with title "Todo to Delete"
    When I delete the todo
    Then the todo should be removed from the system

  Scenario: Toggle todo completion status
    Given I have a todo with title "Toggle Test" that is not completed
    When I toggle the completion status
    Then the todo should be marked as completed
    When I toggle the completion status again
    Then the todo should be marked as not completed

  Scenario: Filter todos by completion status
    Given I have created todos with different completion status:
      | title         | completed |
      | Active Task 1 | false     |
      | Active Task 2 | false     |
      | Done Task 1   | true      |
    When I filter todos by completion status "false"
    Then I should get 2 todos
    And all returned todos should be not completed

  Scenario: Search todos by title
    Given I have created todos:
      | title                    |
      | Project Management Task  |
      | Project Documentation    |
      | Team Meeting            |
    When I search for todos with title containing "Project"
    Then I should get 2 todos
    And all returned todos should have "Project" in their title