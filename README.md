# StickyUp

StickyUp is a sticky-note workspace for capturing, arranging, and organizing ideas. The project is a Java web application packaged as a Maven WAR.

## Features

- Create an account and sign in
- Organize notes in a workspace
- Recover an account password with a one-time email link
- Browse the interactive landing-page demo

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

## Password reset email

Password recovery sends a one-time link that expires after 30 minutes. Configure these environment variables on the server:

| Variable | Purpose |
| --- | --- |
| `SMTP_HOST` | SMTP server host |
| `SMTP_PORT` | SMTP port; defaults to `587` |
| `SMTP_USERNAME` | SMTP account name |
| `SMTP_PASSWORD` | SMTP password or app password |
| `MAIL_FROM` | Verified sender address |
| `APP_BASE_URL` | Public HTTPS origin, without a trailing slash |

Keep mail credentials in the server's secret/environment-variable settings. Do not commit them to the repository. More details are in [`PASSWORD-RESET-SETUP.txt`](PASSWORD-RESET-SETUP.txt).

## Deployment

The repository includes a `Dockerfile` for building the WAR and running it on Tomcat. [`DEPLOY-RAILWAY.md`](DEPLOY-RAILWAY.md) describes the Railway environment-variable setup; the same database variables can be supplied by another host.

## Security note

The current account implementation stores passwords as plain text. This project is suitable for development and demonstration only until password hashing is implemented. Do not use real user accounts or real passwords with it.

