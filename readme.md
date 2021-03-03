# Tools needed to run the project:
1. PostgreSQL database (db name is in resources/application.properties)
2. Mongo database (db name is in resources/application.properties)
3. Latest Node.js version
4. React dev tools extension in Chrome
5. Redux dev tools extension in Chrome

# Steps:
1. Download and install the stuff mentioned above
2. Create a new table in Postgres with the SQL code below:

        CREATE TABLE oauth2_authorized_client (
          client_registration_id varchar(100) NOT NULL,
          principal_name varchar(200) NOT NULL,
          access_token_type varchar(100) NOT NULL,
          access_token_value bytea NOT NULL,
          access_token_issued_at timestamp NOT NULL,
          access_token_expires_at timestamp NOT NULL,
          access_token_scopes varchar(1000) DEFAULT NULL,
          refresh_token_value bytea DEFAULT NULL,
          refresh_token_issued_at timestamp DEFAULT NULL,
          created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
          PRIMARY KEY (client_registration_id, principal_name)
        );

2. Run Postgres (you might need to alter settings in resources/application.properties) and Mongo
3. Navigate to src/.../frontend folder and install all packages with 'npm install'
4. Run "npm run wp" to generate the webpack
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
- redux
- css modules
- react icons