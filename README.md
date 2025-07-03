# 🚗 Vehicle Management System

A full-stack web application for managing vehicles, built with **Spring Boot**, **Angular**, and **MySQL**, designed with clean architecture and secure deployment on **AWS**.

🔗 **Live Demo:** [http://vehicle-management-front.s3-website-us-east-1.amazonaws.com/](http://vehicle-management-front.s3-website-us-east-1.amazonaws.com/)
---

## 🛠 Tech Stack

- **Frontend:** Angular
- **Backend:** Spring Boot (REST API)
- **Database:** MySQL (RDS)
- **Authentication:** JWT-based auth
- **Deployment:** EC2 (Backend), S3 (Angular frontend), RDS (MySQL)

## ⚙️ Environment Setup

### 🔐 Spring Profiles

- `application-local.properties`: for local development
- `application-aws.properties`: for AWS deployment

## 🚀 How to Run the Project

### 🧱 Backend - Spring Boot

#### 🔹 For **Local Development**

1. Run directly from your IDE with profile set to `local`
2. OR run via command line:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local

#### 🔹 For **Production Deployment (AWS)**

1. Run via command line:
   ```bash
   mvn clean package -Paws -DskipTests

### 🧱 Frontend - Angular

1. Run via command line:
   ```bash
   ng build --configuration production

Upload the dist/Vehicle_Management/ output to your S3 bucket for hosting

### 🔑 Initial Root Login Setup**

After starting the Spring Boot backend for the first time, execute the following SQL script in your MySQL database to create the default root user:

```sql
INSERT INTO users (   is_deleted,   email,   first_time_login,   full_name,   password,   role,   user_id ) VALUES (   b'0',     'root_admin@example.com',   b'1',     'Root Administrator',   '$2a$10$TpAqWw.MzULR.YvPAgxmU.axR2wE/iJnWOjGucM2QcEMom8mIAl.e',   'ROOT_ADMIN',   'root_admin' );
```
🔐 User ID: root_admin

🔐 Password: rootadminPassword

Note: The password is bcrypt-hashed, and JWT-based login is implemented.

### 🛡 Security Practices

Sensitive properties (e.g., application-aws.properties) are excluded from version control using .gitignore

Production secrets should be stored via environment variables or AWS Systems Manager (SSM)

### 🙋‍♂️ Author
Sai Pranith Arandkar
