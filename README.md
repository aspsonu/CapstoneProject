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

---


---


---

## ⚙️ Environment Setup

### 🔐 Spring Profiles

- `application-local.properties`: for local development
- `application-aws.properties`: for AWS deployment

---

## 🚀 How to Run the Project

### 🧱 Backend - Spring Boot

#### 🔹 For **Local Development**

1. Run directly from your IDE with profile set to `local`
2. OR run via command line:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local

#### 🔹 For **Prod Deployment (AWS)**

```bash
mvn clean package -Paws -DskipTests

This generates a .war file in the target/ directory and then Deploy the .war to Tomcat (e.g., on EC2)
