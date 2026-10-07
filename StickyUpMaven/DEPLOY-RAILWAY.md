# Deploy StickyUp to Railway

This project is a Maven WAR using Jakarta Servlet 6.0, Spring MVC, Hibernate, and MySQL. The Dockerfile builds the WAR and runs it as the root app in Tomcat 10.1 on port 8080.

## 1. Push the project to GitHub

Create a private repository and push the project root. Keep it private: local development database settings are in src/main/resources/hibernate.cfg.xml.

## 2. Create the Railway services

1. Create a Railway project and deploy the GitHub repository as a service. Railway detects the Dockerfile in the project root.
2. Add a MySQL service to the same Railway project.
3. In the web service Variables tab, add the entries below. If you rename the MySQL service, replace MySQL in the reference expressions with its Railway service name.

PORT=8080
APP_ENV=production
DB_HOST=${{MySQL.MYSQLHOST}}
DB_PORT=${{MySQL.MYSQLPORT}}
DB_NAME=${{MySQL.MYSQLDATABASE}}
DB_USER=${{MySQL.MYSQLUSER}}
DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}

The Hibernate configuration reads these variables at startup. DB_URL can be used instead of DB_HOST, DB_PORT, and DB_NAME if you prefer to provide a complete JDBC URL. Local development continues to use the values in hibernate.cfg.xml when deployment variables are absent.

## 3. Publish the app

After the first successful deploy, open the web service Settings, then Networking, and generate a Railway domain. Set the service public HTTP port to 8080 if Railway asks which port to expose.

## Before inviting real users

The current account code stores passwords as plain text. Do not use it with real user accounts until password hashing has been added and existing test passwords have been replaced. Rotate any database password that has been shared or committed, and set the new value only in Railway Variables.