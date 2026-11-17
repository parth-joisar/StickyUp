# Deploy StickyUp to Railway

The Maven WAR app lives in `Implementation/StickyUpMaven`. Its Dockerfile builds the WAR and runs it with Tomcat 10.1 on port 8080.

## 1. Set up the project source

For a Railway GitHub source, set the service Root Directory to `/Implementation/StickyUpMaven` so Railway finds the Dockerfile and Maven project. If deploying with the Railway CLI, run it from `Implementation/StickyUpMaven`.

Keep the repository private: local database settings are in `Implementation/StickyUpMaven/src/main/resources/hibernate.cfg.xml`.

## 2. Create the Railway services

1. Create a Railway project and deploy the app from `Implementation/StickyUpMaven`.
2. Add a MySQL service to the same Railway project.
3. In the web service Variables tab, add the entries below. If you rename the MySQL service, replace MySQL in the reference expressions with its Railway service name.

```text
PORT=8080
APP_ENV=production
DB_HOST=${{MySQL.MYSQLHOST}}
DB_PORT=${{MySQL.MYSQLPORT}}
DB_NAME=${{MySQL.MYSQLDATABASE}}
DB_USER=${{MySQL.MYSQLUSER}}
DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}
```

The Hibernate configuration reads these variables at startup. DB_URL can be used instead of DB_HOST, DB_PORT, and DB_NAME if you prefer to provide a complete JDBC URL. Local development continues to use the values in hibernate.cfg.xml when deployment variables are absent.

## 3. Publish the app

After the first successful deploy, open the web service Settings, then Networking, and generate a Railway domain. Set the service public HTTP port to 8080 if Railway asks which port to expose.

## 4. Session management

The workspace profile menu includes a Sessions panel to view the browser/operating system for active logins and sign out other devices. Sessions expire after 30 minutes of server inactivity. Logout uses a POST request, protected workspace responses are not cached, and session cookies are HttpOnly.

The active-session list is held in the running app's memory; it adds no database table. A Railway restart clears the list and ends existing app sessions. If you scale the service to multiple replicas, use a shared session store before relying on a complete cross-device list.

## 5. Review manual password reset requests

If SMTP is not configured or reset email delivery fails, the forgot-password form saves a request in MySQL without storing the password or reset token. The user receives a generic confirmation. Requests are saved for review even when the email does not match an account.

In the Railway MySQL query console, list pending requests:

```sql
SELECT id, email, requested_at, status
FROM password_reset_requests
WHERE status = 'PENDING'
ORDER BY requested_at ASC;
```

After reviewing a request, mark it reviewed:

```sql
UPDATE password_reset_requests
SET status = 'REVIEWED'
WHERE id = REQUEST_ID;
```

Only handle requests for verified account owners. Password values are currently stored in plain text by this demo application; do not use real or reused passwords.
