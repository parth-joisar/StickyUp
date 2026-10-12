# StickyUp

StickyUp is a sticky-note workspace for capturing, arranging, and organizing ideas. The project is a Java web application packaged as a Maven WAR.

## Features

- Create an account and sign in
- Organize notes in a workspace
- Browse the interactive landing-page demo

## Desktop page previews

<details>
<summary>Landing page</summary>

![StickyUp homepage desktop preview](docs/previews/stickyup-home.svg)

</details>

<details>
<summary>Sign-in page</summary>

![StickyUp login desktop preview](docs/previews/stickyup-login.svg)

</details>

<details>
<summary>Account creation page</summary>

![StickyUp sign-up desktop preview](docs/previews/stickyup-signup.svg)

</details>

## Technology

- Java 17
- Maven WAR packaging
- Spring MVC 6
- Hibernate 6 and Jakarta Persistence
- MySQL
- Tomcat 10.1 / Jakarta Servlet 6

## Run locally

1. Install JDK 17, Maven, and MySQL Server.
2. Create the local database and user using [`database-setup.sql`](database-setup.sql). See [`DATABASE-SETUP.txt`](DATABASE-SETUP.txt) for details.
3. The default local database settings are in `src/main/resources/hibernate.cfg.xml`. Set `DB_URL`, or `DB_HOST`, `DB_PORT`, and `DB_NAME`, plus `DB_USER` and `DB_PASSWORD`, to override them.
4. Build the WAR:

   ```bash
   mvn clean package
   ```

5. Deploy `target/StickyUp.war` to Tomcat 10.1. Open the Tomcat address in a browser and choose **Start creating**.

## Deployment

The repository includes a `Dockerfile` for building the WAR and running it on Tomcat. [`DEPLOY-RAILWAY.md`](DEPLOY-RAILWAY.md) describes the Railway environment-variable setup; the same database variables can be supplied by another host.

## Security note

The current account implementation stores passwords as plain text. This project is suitable for development and demonstration only until password hashing is implemented. Do not use real user accounts or real passwords with it.
