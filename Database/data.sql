-- Clear existing data
DELETE FROM todos;

-- Reset sequence
ALTER TABLE todos ALTER COLUMN id RESTART WITH 1;

-- Insert seed data
INSERT INTO todos (title, description, completed, due_date, created_at, updated_at)
VALUES 
    ('Complete project documentation', 'Write comprehensive documentation for the Todo application', false, DATEADD('DAY', 7, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Review code changes', 'Review and test all recent code changes', false, DATEADD('DAY', 2, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Setup CI/CD pipeline', 'Configure continuous integration and deployment pipeline', false, DATEADD('DAY', 5, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Update dependencies', 'Check and update project dependencies to latest versions', true, DATEADD('DAY', -1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Write unit tests', 'Add unit tests for the new features', false, DATEADD('DAY', 3, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Implement error handling', 'Add proper error handling and validation', false, DATEADD('DAY', 4, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Optimize database queries', 'Review and optimize database queries for better performance', true, DATEADD('DAY', -2, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Security audit', 'Perform security audit and fix vulnerabilities', false, DATEADD('DAY', 6, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('User feedback implementation', 'Implement changes based on user feedback', false, DATEADD('DAY', 8, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Deploy to production', 'Deploy the latest changes to production', false, DATEADD('DAY', 10, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);