# Tools needed to run the project:
1. PostgreSQL database (db name is in resources/application.properties)
2. Mongo database (db name is in resources/application.properties)
3. Latest Node.js version

# Steps:
1. Download and install the stuff mentioned above
2. Run Postgres and Mongo
3. Navigate to src/.../frontend folder and install all packages with 'npm install'
4. Run src/.../TcsProjectGradingApplication.java
5. Navigate to localhost:8080/ in a browser to see the app
5. Optional: start webpack watcher with 'npm run watch' (from src/.../frontend) to see changes to frontend code applied instantly
6. Optional: turn on linting (config is in .eslintrc.json) in your IDE to enable error/style/etc checking for frontend code

# Main tools and frameworks used:
- mongodb
- postgresql
- spring (boot)
- jackson + jpa

- webpack
- react
- react router
- react bootstrap
- css modules