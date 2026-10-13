# 🛒 AzCart — E-Commerce Microservices Backend

AzCart is an e-commerce backend built as **Spring Boot microservices**. It handles authentication (JWT + Google/Amazon OAuth2), product catalog, cart, orders with **Razorpay** payments, product comments, and email notifications, with separate **USER** and **ADMIN** roles.

Services talk to each other through **OpenFeign** (synchronous) and **Apache Kafka** (asynchronous events), are discovered through **Eureka**, and are exposed through a single **Spring Cloud API Gateway** that validates JWTs.

> **Status:** Under active development. It runs locally; it is not deployed yet.
> **Frontend:** The React frontend is not part of this repository. It is expected at `http://localhost:5173`.
> FrontEnd Repository For This Project : https://github.com/ImGowtham1205/E-Commerce-Frontend.git

---

## 📑 Table of Contents

- [Architecture](#-architecture)
- [Services and Ports](#-services-and-ports)
- [Tech Stack](#-tech-stack)
- [Features](#-features)
- [Kafka Events](#-kafka-events)
- [Run Locally (Step by Step)](#-run-locally-step-by-step)
- [Environment Variables](#-environment-variables)
- [API Reference](#-api-reference)
- [Troubleshooting](#-troubleshooting)
- [Project Structure](#-project-structure)
- [Engineering Highlights](#-engineering-highlights)
- [Roadmap](#-roadmap)
- [Author](#-author)

---

## 🏗 Architecture

```
                      ┌──────────────────────┐
                      │  React Frontend      │  (separate, :5173)
                      └──────────┬───────────┘
                                 │  Bearer JWT
                      ┌──────────▼───────────┐        ┌──────────────────┐
                      │  API Gateway :8765   │◄──────►│ Service Registry │
                      │  JWT validation      │        │  (Eureka) :8761  │
                      └──────────┬───────────┘        └──────────────────┘
      ┌──────────────┬───────────┼────────────┬──────────────┐
      ▼              ▼           ▼            ▼              ▼
┌───────────┐ ┌────────────┐ ┌──────────┐ ┌───────────┐ ┌──────────┐
│ authservice│ │  product-  │ │  cart-   │ │  order-   │ │ comment  │
│   :8080   │ │  service   │ │ service  │ │  service  │ │  :8060   │
│           │ │   :8090    │ │  :8070   │ │   :8100   │ │          │
└─────┬─────┘ └─────┬──────┘ └────┬─────┘ └─────┬─────┘ └────┬─────┘
    MySQL         MySQL        MongoDB        MySQL        MongoDB
      │             │             │             │             │
      └─────────────┴──── Redis (cache) ────────┴─────────────┘
      │             │             │             │             │
      └─────────────┴───── Kafka (events) ──────┴─────────────┘
                                  │
                         ┌────────▼────────┐
                         │  MailService    │  → Gmail SMTP
                         │     :8050       │
                         └─────────────────┘
```

Every request goes through the gateway, which checks the `Authorization: Bearer <token>` header, refreshes tokens that are close to expiring (returned in the `X-Auth-Token` response header), and forwards the user's id and role to downstream services.

---

## 🧩 Services and Ports

| Service | Folder | Port | Database | Responsibility |
|---|---|---|---|---|
| Service Registry | `service-registry` | 8761 | – | Eureka server for service discovery |
| API Gateway | `api-gateway` | 8765 | Redis | Routing, CORS, JWT validation and refresh |
| Auth Service | `authservice` | 8080 (default) | MySQL `authservice` | Register, login, JWT, Google/Amazon OAuth2, password reset with OTP, logout token blacklist, account deletion |
| Product Service | `product-service` | 8090 | MySQL `products` | Product CRUD (admin), category browsing, pagination, image upload/serve, stock updates |
| Cart Service | `cart-service` | 8070 | MongoDB `cart-db` | Add/update/remove cart items, cart total |
| Order Service | `order-service` | 8100 | MySQL `orders` | Purchases, Razorpay payment create/verify, order history, cancellation |
| Comment Service | `comment` | 8060 | MongoDB `comment-db` | Add/update/delete/list product comments |
| Mail Service | `MailService` | 8050 | – | Consumes Kafka events and sends emails through Gmail SMTP |

Redis (`localhost:6379`) is used for caching by the gateway, auth, product, cart, order, and comment services.

---

## 🔧 Tech Stack

- **Language / Runtime:** Java 21
- **Framework:** Spring Boot 4.0.x (Spring Boot 3.2.x for the gateway), Spring Cloud 2025.1.x (2023.0.x for the gateway)
- **Security:** Spring Security, JWT (jjwt 0.12.6), OAuth2 client (Google, Amazon), BCrypt
- **Microservices:** Netflix Eureka, Spring Cloud Gateway, OpenFeign, Resilience4j (circuit breaker and time limiter)
- **Messaging:** Apache Kafka
- **Databases:** MySQL (Spring Data JPA / Hibernate), MongoDB (Spring Data MongoDB)
- **Caching:** Redis
- **Payments:** Razorpay (`razorpay-java` 1.4.8)
- **Email:** Spring Mail (Gmail SMTP)
- **Build:** Maven (wrapper included, no global Maven install needed)

---

## ✨ Features

### 👤 User
- Register and log in with email/password, or with **Google / Amazon**
- Complete profile after OAuth sign-in
- Browse products by category, with pagination
- Add to cart, update quantity, remove items, view cart total
- Buy products and pay with **Razorpay**; cancel orders
- Add, edit, and delete product comments
- Forgot/reset/change password with email OTP
- Email notifications for signup, password reset, orders, and account deletion

### 🛠 Admin
- Register with a secret admin key
- Add, update, and delete products (with image upload)
- View all orders
- Admin-specific account, password, and logout endpoints

### ⚙️ Platform
- Stateless JWT authentication, 30-minute tokens with automatic refresh at the gateway
- Role-based access (`ROLE_USER`, `ROLE_ADMIN`)
- Logout token blacklist
- Redis caching with dedicated cache service classes
- Circuit breakers and time limits on inter-service calls
- Event-driven cleanup: deleting a user or product removes related cart items and comments through Kafka
- Global exception handlers in every service

---

## 📨 Kafka Events

| Topic | Produced by | Consumed by |
|---|---|---|
| `user.created` | authservice | MailService |
| `admin.created` | authservice | MailService |
| `user.forgot-password` | authservice | MailService |
| `admin.forgot-password` | authservice | MailService |
| `user.deleted` | authservice | MailService, cart-service, comment |
| `admin.deleted` | authservice | MailService |
| `order.created` | order-service | MailService |
| `order.cancelled` | order-service | MailService |
| `product.deleted` | product-service | cart-service, comment |

---

## 🚀 Run Locally (Step by Step)

### Step 1: Install prerequisites

| Tool | Version | Check |
|---|---|---|
| JDK | 21 | `java -version` |
| Git | any | `git --version` |
| Docker Desktop | any | `docker --version` (required for Kafka; optional for MySQL, MongoDB, Redis) |

You do **not** need to install Maven; each service includes `mvnw` (Linux/macOS) and `mvnw.cmd` (Windows).

You will also need:
- A **Gmail account with an App Password** (for emails)
- **Razorpay test keys** (from the Razorpay dashboard)
- **Google OAuth2 credentials** (optional, only for Google login)
- **Amazon Login credentials** (optional, only for Amazon login)

Step 5 explains how to get the Google, Amazon, Razorpay, and Gmail credentials.

### Step 2: Clone the repository

```bash
git clone https://github.com/ImGowtham1205/E-commerce-backend-microservices.git
cd E-commerce-backend-microservices
```

### Step 3: Set up Kafka (Docker)

Kafka runs as a **two-broker KRaft cluster** (no ZooKeeper) with real replication. The services are already configured for `localhost:9092` and `localhost:9094`, which match the ports below.

**3.1** Create a folder named `docker` in the project root, and inside it create a file named `kafka-compose.yml`:

```yaml
# Two-broker Kafka KRaft cluster with real replication
# Reachable from applications running directly on the host machine.

networks:
  kafka-net:
    driver: bridge

volumes:
  kafka0-data:
  kafka1-data:

services:

  # =========================
  # Kafka Broker 0
  # =========================
  kafka0:
    image: apache/kafka:4.0.0
    container_name: kafka0
    hostname: kafka0

    networks:
      - kafka-net

    ports:
      - "9092:9092"

    environment:
      CLUSTER_ID: "ZHN6v8qcQJm8N-6f6s77Ng"

      KAFKA_NODE_ID: 0
      KAFKA_PROCESS_ROLES: broker,controller

      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER

      KAFKA_CONTROLLER_QUORUM_VOTERS: "0@kafka0:9093,1@kafka1:9093"

      KAFKA_LISTENERS: "PLAINTEXT://:19092,CONTROLLER://:9093,PLAINTEXT_HOST://:9092"

      KAFKA_ADVERTISED_LISTENERS: "PLAINTEXT://kafka0:19092,PLAINTEXT_HOST://localhost:9092"

      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT"

      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT

      # Store Kafka data inside the persistent Docker volume
      KAFKA_LOG_DIRS: /var/lib/kafka/data

      # Replication
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 2
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 2
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_DEFAULT_REPLICATION_FACTOR: 2
      KAFKA_MIN_INSYNC_REPLICAS: 1

      # Partitions
      KAFKA_NUM_PARTITIONS: 3

      # Keep Kafka messages indefinitely
      KAFKA_LOG_RETENTION_MS: -1

    volumes:
      - kafka0-data:/var/lib/kafka/data


  # =========================
  # Kafka Broker 1
  # =========================
  kafka1:
    image: apache/kafka:4.0.0
    container_name: kafka1
    hostname: kafka1

    networks:
      - kafka-net

    ports:
      - "9094:9092"

    environment:
      CLUSTER_ID: "ZHN6v8qcQJm8N-6f6s77Ng"

      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller

      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER

      KAFKA_CONTROLLER_QUORUM_VOTERS: "0@kafka0:9093,1@kafka1:9093"

      KAFKA_LISTENERS: "PLAINTEXT://:19092,CONTROLLER://:9093,PLAINTEXT_HOST://:9092"

      KAFKA_ADVERTISED_LISTENERS: "PLAINTEXT://kafka1:19092,PLAINTEXT_HOST://localhost:9094"

      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT"

      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT

      # Store Kafka data inside the persistent Docker volume
      KAFKA_LOG_DIRS: /var/lib/kafka/data

      # Replication
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 2
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 2
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_DEFAULT_REPLICATION_FACTOR: 2
      KAFKA_MIN_INSYNC_REPLICAS: 1

      # Partitions
      KAFKA_NUM_PARTITIONS: 3

      # Keep Kafka messages indefinitely
      KAFKA_LOG_RETENTION_MS: -1

    volumes:
      - kafka1-data:/var/lib/kafka/data
```

**3.2** Start the cluster from the project root:

```bash
docker compose -f docker/kafka-compose.yml up -d
```

**3.3** Check that both brokers are running:

```bash
docker ps
```

You should see `kafka0` and `kafka1` with status `Up`.

**3.4** (Optional) Confirm the cluster responds. The topic list is empty until the services publish their first events, because topics are created automatically on first use:

```bash
docker exec -it kafka0 /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka0:19092 --list
```

> **Keep both brokers running.** With two controller nodes, the cluster needs both of them to agree. If you stop one broker, the other cannot accept new messages until the first one is back.

---

### Step 4: Set up MySQL, MongoDB, and Redis

The services need three data stores:

| Store | Used by | Databases |
|---|---|---|
| MySQL | authservice, product-service, order-service | `authservice`, `products`, `orders` |
| MongoDB | cart-service, comment | `cart-db`, `comment-db` (created automatically on first write) |
| Redis | gateway and all services (cache) | none to create |

Pick **one** of the two options below.

#### Option A: Install MySQL and MongoDB directly on your machine

**A1. Install MySQL 8**

- **Windows:** Download **MySQL Installer** from [dev.mysql.com/downloads/installer](https://dev.mysql.com/downloads/installer/). Choose *Server only* (or *Developer Default*), keep port `3306`, and set a root password when asked. Let it run as a Windows service.
- **macOS (Homebrew):**
  ```bash
  brew install mysql
  brew services start mysql
  ```
- **Ubuntu / Debian:**
  ```bash
  sudo apt update
  sudo apt install mysql-server
  sudo systemctl start mysql
  ```

Verify it is running:

```bash
mysql --version
mysql -uroot -p -e "SELECT 1;"
```

> On a fresh Ubuntu install, the root user may use socket login. If `mysql -uroot -p` is refused, run `sudo mysql` and set a password:
> `ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root'; FLUSH PRIVILEGES;`

**A2. Create the three databases**

```bash
mysql -uroot -p -e "CREATE DATABASE authservice; CREATE DATABASE products; CREATE DATABASE orders;"
```

Hibernate creates the tables automatically (`ddl-auto=update`). Remember the username and password you used, because they become `DB_USERNAME` and `DB_PASSWORD` in Step 7.

**A3. Install MongoDB Community Edition**

- **Windows:** Download the MSI from [mongodb.com/try/download/community](https://www.mongodb.com/try/download/community). In the installer choose *Complete*, tick **Install MongoDB as a Service**, and keep port `27017`. MongoDB Compass (a GUI) is optional.
- **macOS (Homebrew):**
  ```bash
  brew tap mongodb/brew
  brew install mongodb-community
  brew services start mongodb-community
  ```
- **Ubuntu / Debian:** Follow the official guide for your Ubuntu version: [mongodb.com/docs/manual/administration/install-on-linux](https://www.mongodb.com/docs/manual/administration/install-on-linux/). Then start it with `sudo systemctl start mongod`.

Verify it is running (install [`mongosh`](https://www.mongodb.com/try/download/shell) if the command is missing):

```bash
mongosh --eval "db.runCommand({ ping: 1 })"
```

No setup is needed for `cart-db` and `comment-db`; MongoDB creates them when the services first save data. The services connect without a username or password, so keep MongoDB's default local setup (authentication off).

**A4. Run Redis in Docker**

Redis has no official Windows installer, so the simplest cross-platform way is a single container:

```bash
docker run -d --name azcart-redis -p 6379:6379 redis:7
```

(On Linux/macOS you can instead install it natively: `sudo apt install redis-server` or `brew install redis`, then `brew services start redis`.)

#### Option B: Run MySQL, MongoDB, and Redis in Docker

**B1. Install Docker Desktop** from [docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop/) and make sure it is running. Check with:

```bash
docker --version
docker compose version
```

**B2. Create the compose file.** Inside the `docker` folder, create `db-compose.yml`:

```yaml
# MySQL + MongoDB + Redis for local development only.
# The root / root credentials are NOT safe for anything else.

volumes:
  mysql-data:
  mongo-data:
  redis-data:

services:

  mysql:
    image: mysql:8
    container_name: azcart-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql

  mongo:
    image: mongo:7
    container_name: azcart-mongo
    ports:
      - "27017:27017"
    volumes:
      - mongo-data:/data/db

  redis:
    image: redis:7
    container_name: azcart-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
```

**B3. Start the containers** from the project root:

```bash
docker compose -f docker/db-compose.yml up -d
```

**B4. Wait for MySQL to finish starting** (about 20 to 30 seconds on the first run). Watch the logs until you see `ready for connections`:

```bash
docker logs -f azcart-mysql
```

Press `Ctrl + C` to leave the logs (the container keeps running).

**B5. Create the three MySQL databases:**

```bash
docker exec -it azcart-mysql mysql -uroot -proot -e "CREATE DATABASE authservice; CREATE DATABASE products; CREATE DATABASE orders;"
```

**B6. Verify everything:**

```bash
docker ps
docker exec -it azcart-mysql mysql -uroot -proot -e "SHOW DATABASES;"
docker exec -it azcart-mongo mongosh --eval "db.runCommand({ ping: 1 })"
docker exec -it azcart-redis redis-cli ping
```

You should see the three databases listed, `ok: 1` from MongoDB, and `PONG` from Redis. Use `DB_USERNAME=root` and `DB_PASSWORD=root` in Step 7.

**Useful Docker commands**

| Task | Command |
|---|---|
| Stop containers (keep data) | `docker compose -f docker/db-compose.yml stop` |
| Start them again | `docker compose -f docker/db-compose.yml start` |
| Remove containers (keep data volumes) | `docker compose -f docker/db-compose.yml down` |
| Remove containers **and delete all data** | `docker compose -f docker/db-compose.yml down -v` |

---

### Step 5: Get your Google, Amazon, Razorpay, and Gmail credentials

You need four sets of credentials. They map to the environment variables you will set in Step 7:

| Credential | Environment variables | Needed for |
|---|---|---|
| Google OAuth client | `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET` | "Login with Google" |
| Amazon Login security profile | `AMAZON_CLIENT_ID`, `AMAZON_CLIENT_SECRET` | "Login with Amazon" |
| Razorpay test API keys | `RAZORPAY_CLIENT_ID`, `RAZORPAY_CLIENT_SECRET` | Payments |
| Gmail App Password | `EMAIL`, `EMAIL_PASSWORD` | Sending emails from `MailService` |

The two social logins are optional. If you skip them, set placeholder values (such as `dummy`) so `authservice` can still start. Razorpay is needed to test payments, and the Gmail App Password is needed for emails (signup, password reset, orders).

> **Keep secrets private.** Never commit client secrets or API keys to Git, paste them in screenshots, or share them. If one leaks, regenerate it in the provider's dashboard. The provider consoles change their layout from time to time, so if a menu name differs slightly, follow the official docs linked in each part.

The redirect URIs below assume you run the gateway locally on port `8765`, as in this guide.

#### 5.1 Google OAuth2 (Login with Google)

1. Go to the [Google Cloud Console](https://console.cloud.google.com/) and sign in with your Google account.
2. Click the project picker at the top and choose **New Project**. Name it (for example `azcart`), then click **Create** and make sure it is the selected project.
3. Open the menu and go to **Google Auth Platform**. If you see "Google Auth Platform not configured yet", click **Get Started**.
4. Fill in the **App Information**: an app name (for example `AzCart`) and your support email.
5. For **Audience**, choose **External**.
6. Add your contact email, accept the user data policy, and click **Create**.
7. Open **Audience** in the left menu. While the app is in **Testing** status, only listed test users can sign in. Under **Test users**, click **Add users** and add the Gmail address(es) you will log in with.
8. Open **Clients** in the left menu and click **Create client**.
9. Set **Application type** to **Web application** and give the client a name (for example `AzCart Web`).
10. Leave **Authorized JavaScript origins** empty.
11. Under **Authorized redirect URIs**, click **Add URI** and enter exactly:
    ```
    http://localhost:8765/authservice/login/oauth2/code/google
    ```
12. Click **Create**. A dialog shows your **Client ID** and **Client secret**. Copy both now.
    - `GOOGLE_CLIENT_ID` = the Client ID
    - `GOOGLE_CLIENT_SECRET` = the Client secret

It can take a few minutes for a new client to start working. The app only asks for `openid`, `email`, and `profile`, so you do not need to add extra scopes. If you lose the secret later, open the client in **Clients** and add or reset a secret there.

Official guide: [Google Auth Platform documentation](https://developers.google.com/identity/protocols/oauth2/web-server)

#### 5.2 Amazon Login (Login with Amazon)

1. Go to the [Login with Amazon console](https://developer.amazon.com/loginwithamazon/console/site/lwa/overview.html) and sign in with an Amazon account. If asked, complete the developer account registration.
2. Click **Create a New Security Profile**.
3. Fill in:
   - **Security Profile Name:** for example `AzCart`
   - **Security Profile Description:** for example `AzCart e-commerce login`
   - **Consent Privacy Notice URL:** any page you control that has a privacy notice (your portfolio or GitHub page is fine for a personal project)
4. Click **Save**.
5. In the list of security profiles, find yours, click the gear (**Manage**) icon, and choose **Web Settings**.
6. Click **Edit** and fill in:
   - **Allowed Origins:**
     ```
     http://localhost:8765
     ```
   - **Allowed Return URLs:**
     ```
     http://localhost:8765/authservice/login/oauth2/code/amazon
     ```
7. Click **Save**.
8. Back on the **Web Settings** tab, copy the **Client ID**, then click **Show Secret** and copy the **Client Secret**.
    - `AMAZON_CLIENT_ID` = the Client ID
    - `AMAZON_CLIENT_SECRET` = the Client Secret

The app requests the `profile` scope, which matches `application.properties`. If Amazon rejects the `http://localhost` URLs, check the current rules in Amazon's docs for allowed return URLs.

Official guide: [Login with Amazon documentation](https://developer.amazon.com/docs/login-with-amazon/documentation-overview.html)

#### 5.3 Razorpay (payments)

1. Sign up at [razorpay.com](https://razorpay.com/) and log in to the Dashboard. You can use **Test Mode** right away, without waiting for KYC.
2. Switch the Dashboard to **Test Mode** using the mode toggle at the top.
3. Go to **Account & Settings**, then **API Keys** (under *Website and app settings*).
4. Click **Generate Key**.
5. A pop-up shows your **Key Id** and **Key Secret**. Download or copy both immediately; the secret is shown only once, and afterwards the dashboard shows only the Key Id.
    - `RAZORPAY_CLIENT_ID` = the Key Id (it starts with `rzp_test_`)
    - `RAZORPAY_CLIENT_SECRET` = the Key Secret

Notes:
- Only the account **Owner** or **Admin** can see API keys.
- Test and Live modes have separate keys. Use only **test** keys while developing; no real money moves.
- Never put the Key Secret in the frontend. The frontend only needs the public Key Id to open Razorpay Checkout.
- The backend creates orders in **INR** and verifies the payment signature with your key secret, so the order-service and the frontend must use keys from the same Razorpay account and mode.

**Test a payment**

In Test Mode, Razorpay Checkout opens a mock payment page. Use these details:

| Method | What to enter |
|---|---|
| UPI (success) | `success@razorpay` |
| UPI (failure) | `failure@razorpay` |
| Card, net banking, wallet | Use Razorpay's test card list, then click **Success** or **Failure** on the mock bank page |

Official test card list: [Razorpay test card details](https://razorpay.com/docs/payments/payments/test-card-details/)

Official guide: [Razorpay API keys documentation](https://razorpay.com/docs/payments/dashboard/account-settings/api-keys/)

#### 5.4 Gmail App Password (MailService)

`MailService` sends emails through Gmail SMTP (`smtp.gmail.com:587`). Gmail does not accept your normal password for this, so you need a 16-character **App Password**.

**Before you start:** use a personal Gmail account. App passwords are only available when 2-Step Verification is on, and Google may hide the option for work or school accounts, accounts that use security keys only for 2-Step Verification, and accounts with Advanced Protection.

1. Sign in at [myaccount.google.com](https://myaccount.google.com/) and open **Security**.
2. Under **How you sign in to Google**, click **2-Step Verification** and turn it on if it is not already on (follow Google's prompts to add a phone or authenticator app).
3. Open the App passwords page directly: [myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords). Google may ask you to sign in again.
4. Enter a name you will recognize (for example `AzCart MailService`) and click **Create**.
5. Google shows a 16-character password. Copy it now; it is shown only once.
6. Set the values for Step 7:
    - `EMAIL` = your full Gmail address (for example `yourname@gmail.com`)
    - `EMAIL_PASSWORD` = the 16-character App Password (remove the spaces if Google displays it in groups)

Notes:
- Changing your Google account password revokes all your App Passwords. Create a new one and update `EMAIL_PASSWORD`.
- You can delete an App Password any time from the same App passwords page.
- Use the App Password only for this project and never commit it to Git.

Official guide: [Sign in with app passwords](https://support.google.com/accounts/answer/185833)

---

### Step 6: Generate a JWT secret

All services that sign or verify tokens read the secret from the `JWT_SECRET` environment variable. It must be a **Base64-encoded** 256-bit key. This small Java program generates one.

**6.1** Create a file named `KeyGeneration.java` (the file name must match the class name). You can save it anywhere, for example on your Desktop; it does not need to be inside the project.

```java
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeyGeneration {

	public static String keyGeneration() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
			SecretKey secretKey = keyGenerator.generateKey();
			return Base64.getEncoder().encodeToString(secretKey.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String args[]) {
		System.out.println(keyGeneration());
	}

}
```

**6.2** Run it. With JDK 21 you can run the file directly, without compiling first:

```bash
java KeyGeneration.java
```

(Or open the file in IntelliJ, Eclipse, or STS and run the `main` method.)

**6.3** Copy the single line it prints. It is a 44-character Base64 string that ends with `=`. This is your `JWT_SECRET`.

**6.4** Use this **same value** for `JWT_SECRET` in every service that needs it: `api-gateway`, `authservice`, `product-service`, `cart-service`, `order-service`, and `comment`. If the values differ, requests fail with `401 Unauthorized`. You will set it in Step 7.

Notes:
- Run the program **once** and reuse the output. Each run prints a different key, and a new key makes all existing tokens invalid.
- Treat the key like a password. Do not commit it to Git or share it publicly.
- Do not shorten, edit, or add spaces to the output; copy it exactly.

> **Alternative:** If you prefer not to use Java, `openssl rand -base64 32` (Linux/macOS/Git Bash) produces a valid key too.

### Step 7: Set environment variables

The `application.properties` files read secrets from environment variables. Set them in **every terminal** you use to start a service (see [Environment Variables](#-environment-variables) for which service needs which).

**Linux / macOS**

```bash
export JWT_SECRET="<paste-your-base64-secret>"
export ADMIN_SECRET="choose-an-admin-registration-key"
export DB_USERNAME="root"
export DB_PASSWORD="root"
export GOOGLE_CLIENT_ID="<google-client-id>"
export GOOGLE_CLIENT_SECRET="<google-client-secret>"
export AMAZON_CLIENT_ID="<amazon-client-id>"
export AMAZON_CLIENT_SECRET="<amazon-client-secret>"
export RAZORPAY_CLIENT_ID="<razorpay-key-id>"
export RAZORPAY_CLIENT_SECRET="<razorpay-key-secret>"
export EMAIL="<your-gmail-address>"
export EMAIL_PASSWORD="<gmail-app-password>"
```

**Windows (PowerShell)**

```powershell
$env:JWT_SECRET="<paste-your-base64-secret>"
$env:ADMIN_SECRET="choose-an-admin-registration-key"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="root"
$env:GOOGLE_CLIENT_ID="<google-client-id>"
$env:GOOGLE_CLIENT_SECRET="<google-client-secret>"
$env:AMAZON_CLIENT_ID="<amazon-client-id>"
$env:AMAZON_CLIENT_SECRET="<amazon-client-secret>"
$env:RAZORPAY_CLIENT_ID="<razorpay-key-id>"
$env:RAZORPAY_CLIENT_SECRET="<razorpay-key-secret>"
$env:EMAIL="<your-gmail-address>"
$env:EMAIL_PASSWORD="<gmail-app-password>"
```

> **Tip:** If you don't need Google or Amazon login, you can still set placeholder values such as `dummy` so `authservice` starts (the properties file references them). If you use IntelliJ or STS, set these in the Run Configuration's environment variables instead.

The Google, Amazon, Razorpay, and Gmail values come from [Step 5](#step-5-get-your-google-amazon-razorpay-and-gmail-credentials), where you also register these redirect URIs in each provider's console:

```
http://localhost:8765/authservice/login/oauth2/code/google
http://localhost:8765/authservice/login/oauth2/code/amazon
```

### Step 8: Start the services in order

Open **one terminal per service** (with the environment variables set), from the project root.

**Linux / macOS**

```bash
# 1. Service registry (start first)
cd service-registry && ./mvnw spring-boot:run

# 2. API gateway
cd api-gateway && ./mvnw spring-boot:run

# 3. The rest, in any order
cd authservice && ./mvnw spring-boot:run
cd product-service && ./mvnw spring-boot:run
cd cart-service && ./mvnw spring-boot:run
cd order-service && ./mvnw spring-boot:run
cd comment && ./mvnw spring-boot:run
cd MailService && ./mvnw spring-boot:run
```

**Windows**

```powershell
cd service-registry; .\mvnw.cmd spring-boot:run
```

(Use `.\mvnw.cmd spring-boot:run` in every service folder.)

The first start of each service downloads dependencies, so it takes a few minutes.

### Step 9: Verify everything is running

1. Open the Eureka dashboard: **http://localhost:8761**. You should see `API-GATEWAY`, `AUTHSERVICE`, `PRODUCT-SERVICE`, `CART-SERVICE`, `ORDER-SERVICE`, `COMMENT`, and `MAILSERVICE` listed as **UP**.
2. Check the auth service health through the gateway is reachable by registering a user (Step 10).

### Step 10: Try it out

All requests go through the gateway at `http://localhost:8765/<service-name>/...`.

**Register a user**

```bash
curl -X POST http://localhost:8765/authservice/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","phoneno":"9876543210","password":"Test@1234","address":"Chennai","role":"USER"}'
```

**Register an admin** (uses your `ADMIN_SECRET` as `adminkey`)

```bash
curl -X POST http://localhost:8765/authservice/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Admin","email":"admin@example.com","phoneno":"9123456780","password":"Admin@1234","role":"ADMIN","adminkey":"choose-an-admin-registration-key"}'
```

**Log in** (returns `token` and `role`)

```bash
curl -X POST http://localhost:8765/authservice/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test@1234"}'
```

**Call a protected endpoint**

```bash
curl "http://localhost:8765/product-service/api/products?page=0&size=10" \
  -H "Authorization: Bearer <token>"
```

**Add a product as admin** (multipart: `product` is JSON, `file` is the image)

```bash
curl -X POST http://localhost:8765/product-service/api/admin/addproduct \
  -H "Authorization: Bearer <admin-token>" \
  -F 'product={"productname":"Headphones","description":"Wireless","price":1999,"stock":25,"category":"Electronics"};type=application/json' \
  -F "file=@./headphones.jpg"
```

### Step 11 (optional): Run the frontend

Start the React frontend separately on `http://localhost:5173`. The gateway's CORS rules and the OAuth2 success redirect are configured for this exact origin.

### Stopping everything

Press `Ctrl + C` in each service terminal, then stop the containers:

```bash
docker compose -f docker/kafka-compose.yml stop
docker compose -f docker/db-compose.yml stop      # only if you used Docker for the databases (Option B)
```

Use `down` instead of `stop` to remove the containers (data volumes are kept), or `down -v` to also delete all data.

---

## 🔑 Environment Variables

| Variable | Used by | Purpose |
|---|---|---|
| `JWT_SECRET` | api-gateway, authservice, product-service, cart-service, order-service, comment | Base64 secret for signing and verifying JWTs (must match everywhere) |
| `ADMIN_SECRET` | authservice | Admin key required to register an admin account |
| `DB_USERNAME` | authservice, product-service, order-service | MySQL username |
| `DB_PASSWORD` | authservice, product-service, order-service | MySQL password |
| `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET` | authservice | Google OAuth2 |
| `AMAZON_CLIENT_ID`, `AMAZON_CLIENT_SECRET` | authservice | Amazon Login OAuth2 |
| `RAZORPAY_CLIENT_ID`, `RAZORPAY_CLIENT_SECRET` | order-service | Razorpay API keys |
| `EMAIL`, `EMAIL_PASSWORD` | MailService | Gmail address and App Password |

Spring maps these names to the properties in each `application.properties` (for example `DB_USERNAME` fills `${db-username}`).

Default local connections (from `application.properties`): MySQL `localhost:3306`, MongoDB `localhost:27017`, Redis `localhost:6379`, Kafka `localhost:9092`, Eureka `localhost:8761`.

---

## 📡 API Reference

Base URL: `http://localhost:8765`. Paths below include the service prefix used by the gateway. Except for the public routes, every request needs `Authorization: Bearer <token>`.

### Auth Service — `/authservice/auth`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/register` | Public | Register a user or admin (`role`: `USER` / `ADMIN`) |
| POST | `/login` | Public | Log in, returns JWT and role |
| POST | `/forgot-password` | Public | Send password reset OTP by email |
| PUT | `/reset-password` | Public | Reset password with OTP |
| GET | `/oauth2/authorization/google` | Public | Start Google login (Amazon works the same way) |
| GET | `/api/user/home` · `/api/admin/home` | User / Admin | Home data |
| GET | `/api/user/userinfo` · `/api/admin/admininfo` | User / Admin | Current account info |
| PUT | `/api/user/complete-profile` | User | Complete profile after OAuth signup |
| PUT | `/api/user/changepassword` · `/api/admin/changepassword` | User / Admin | Change password |
| POST | `/api/user/logout` · `/api/admin/logout` | User / Admin | Log out (token is blacklisted) |
| DELETE | `/api/user/accountdeletion` · `/api/admin/accountdeletion` | User / Admin | Delete account |
| GET | `/api/getallblacklisttokens` | Authenticated | List blacklisted tokens |

### Product Service — `/product-service/api`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/products?page=0&size=10` | Logged in | Paginated product list |
| GET | `/products/{category}?page=0&size=10` | Logged in | Products by category |
| GET | `/products/details/{id}` | Logged in | Product details |
| GET | `/products/image/{id}` | Public | Product image |
| POST | `/admin/addproduct` | Admin | Add product (multipart: `product`, `file`) |
| PUT | `/admin/updateproduct` | Admin | Update product (`file` optional) |
| DELETE | `/admin/deleteproduct/{id}` | Admin | Delete product |
| PUT | `/updatestock` | Internal | Update stock (used by order-service) |

### Cart Service — `/cart-service`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/user/addtocart` | Add item to cart |
| GET | `/api/user/getcartitem` | Get cart items |
| PUT | `/api/user/updatequantity` | Change item quantity |
| DELETE | `/api/user/deletecartitem` | Remove item |
| GET | `/api/user/carttotal` | Cart total |

### Order Service — `/order-service`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/user/create` | User | Create Razorpay order |
| POST | `/api/user/verify` | User | Verify Razorpay payment |
| POST | `/api/user/purchase/{productid}` | User | Place an order |
| GET | `/api/user/fetchorder` | User | My orders |
| GET | `/api/admin/fetchorders` | Admin | All orders |
| DELETE | `/api/user/cancelorder/{orderid}` | User | Cancel an order |

### Comment Service — `/comment`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/user/addcomment` | Add comment |
| PUT | `/api/user/updatecomment` | Update comment |
| DELETE | `/api/user/deletecomment/{id}` | Delete comment |
| GET | `/api/user/getcomments/{productid}` | List comments for a product |
| GET | `/api/user/commentcount/{productid}` | Comment count for a product |
| GET | `/api/user/getuserid` | Current user id |

---

## 🩹 Troubleshooting

| Problem | Likely cause and fix |
|---|---|
| `Illegal base64 character` or weak key error on startup | `JWT_SECRET` is not valid Base64 or is too short. Generate it again with the `KeyGeneration` program from Step 6 and copy it exactly. |
| `Unknown database 'authservice'` (or `products` / `orders`) | Create the databases as shown in Step 4 (A2 or B5). |
| `Access denied for user` (MySQL) | `DB_USERNAME` / `DB_PASSWORD` do not match your MySQL credentials. |
| `Could not resolve placeholder 'google-client-id'` (or similar) | An environment variable is missing in that terminal. Set it before starting the service. |
| Services missing from the Eureka dashboard | Start `service-registry` first and wait for it to be ready. |
| Kafka `Connection refused` / `node -1 disconnected` warnings | Kafka containers are not running, or ports `9092` / `9094` are blocked. Check `docker ps`. |
| Kafka stops accepting messages after one broker was stopped | The two-node cluster needs both brokers up. Start both with `docker compose -f docker/kafka-compose.yml up -d`. |
| `Communications link failure` (MySQL) | MySQL is not running or still starting. Check `docker ps` (Option B) or your MySQL service (Option A). |
| `MongoTimeoutException` / cannot connect to `localhost:27017` | MongoDB is not running. Start the service (Option A) or the `azcart-mongo` container (Option B). |
| Redis `Connection refused` | Redis is not running on `6379`. Start the `azcart-redis` container. |
| `401 Unauthorized` from the gateway | Missing or expired token, or `JWT_SECRET` differs between services. |
| `Port already in use` | Another process uses that port. Stop it or change `server.port` in that service. |
| No emails arrive, or `Authentication failed` / `535` errors from Gmail | Use a Gmail **App Password** (2-Step Verification must be on), not your normal password, and check `EMAIL` / `EMAIL_PASSWORD` (Step 5.4). |
| OAuth `redirect_uri_mismatch` | Add the exact redirect URIs from Step 5 in the Google/Amazon console. |
| Google shows "Access blocked" or `access_denied` | The app is in **Testing** status and your account is not a test user. Add it under **Google Auth Platform → Audience → Test users** (Step 5.1). |
| Amazon login fails with an invalid return URL error | The **Allowed Return URLs** in your security profile must match `http://localhost:8765/authservice/login/oauth2/code/amazon` exactly (Step 5.2). |
| Razorpay returns 401 / authentication failed | Wrong Key Id or Key Secret, or test and live keys are mixed. Regenerate test keys and update `RAZORPAY_CLIENT_ID` / `RAZORPAY_CLIENT_SECRET` (Step 5.3). |
| Razorpay payment verification fails | The frontend Checkout and the backend must use keys from the same account and mode (both test, or both live). |
| CORS errors in the browser | The gateway only allows the origin `http://localhost:5173`. |

---

## 📁 Project Structure

```
E-commerce-backend-microservices/
├── service-registry/     # Eureka server (8761)
├── api-gateway/          # Spring Cloud Gateway, JWT filter (8765)
├── authservice/          # Auth, OAuth2, users/admins, OTP (8080)
├── product-service/      # Products, images, pagination (8090)
├── cart-service/         # Cart, MongoDB (8070)
├── order-service/        # Orders, Razorpay (8100)
├── comment/              # Comments, MongoDB (8060)
├── MailService/          # Kafka consumer, email sender (8050)
├── docker/               # kafka-compose.yml, db-compose.yml (local setup files you create)
└── README.md
```

Each service follows the same layout: `controller`, `service`, `repository`, `model`, `configuration`, `filter`, `feign`, `exception`.

---

## 🧠 Engineering Highlights

- **Gateway-level security:** one global filter validates JWTs, refreshes tokens near expiry, and passes `X-User-Id` and `X-User-Role` to downstream services.
- **Event-driven cleanup:** deleting a user or product publishes a Kafka event, and the cart and comment services clean up their own data.
- **Caching done carefully:** cache logic lives in separate `*CacheService` classes so Spring's `@Cacheable` / `@CacheEvict` proxies are not bypassed by self-invocation.
- **Resilience:** Resilience4j circuit breakers and time limiters protect service-to-service and mail calls.
- **Kafka error handling:** consumers use `ErrorHandlingDeserializer`, retry failed messages 3 times with a 3-second backoff, and then publish them to a dead-letter topic so one bad message does not block the topic.
- **Separate user and admin flows:** distinct entities, endpoints, OTP flows, and OAuth provider tables for users and admins.

---

## 🗺 Roadmap

- [ ] Dockerfiles and a full `docker-compose` setup for all services
- [ ] Cloud deployment
- [ ] CI/CD with GitHub Actions
- [ ] Unit and integration tests
- [ ] Centralized configuration (Spring Cloud Config)
- [ ] Distributed tracing and monitoring
- [ ] Swagger / OpenAPI documentation

---

## 👨‍💻 Author

**Gowtham K**
B.Sc. Computer Science, Chennai, India

- GitHub: [@ImGowtham1205](https://github.com/ImGowtham1205)
- Portfolio: [gowtham-personal-portfolio.netlify.app](https://gowtham-personal-portfolio.netlify.app)

---

⭐ If this project helped you, consider giving it a star.