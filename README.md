# BiteRight | Nutrition & Activity Tracker ⚖️ 🥗🏋🏻‍♂️
[![Contributors](https://img.shields.io/github/contributors/hbiegacz/BiteRight?color=red)](https://github.com/hbiegacz/BiteRight/graphs/contributors)
[![Commit Activity](https://img.shields.io/badge/Commits-📈%20View%20Graph-orange)](https://github.com/hbiegacz/BiteRight/graphs/commit-activity)
[![GitHub commits](https://badgen.net/github/commits/hbiegacz/BiteRight?color=yellow)](https://github.com/hbiegacz/BiteRight/commit/)
[![Repo Size](https://img.shields.io/github/repo-size/hbiegacz/BiteRight?color=green)](https://github.com/hbiegacz/BiteRight)
[![Lines of Code](https://img.shields.io/badge/Lines%20of%20Code-~250k-blue?logo=git)](https://github.com/hbiegacz/BiteRight)
[![License: MIT](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)

#### *Eat, track, ***succeed***.*

BiteRight is a nutrition and activity tracking application designed for individuals looking to monitor their dietary habits and physical performance. It provides tools for logging daily food and water intake, managing nutritional goals, and tracking exercise data to support a balanced lifestyle.

### Features
- **Nutritional Tracking**: Log meals to calculate daily calorie and macronutrient (protein, carbohydrates, fats) intake
- **Goal Management**: Set and update personal targets for calories, macronutrients, and water consumption
- **Activity Logging**: Record physical exercises to track estimated calories burned
- **Progress Analytics**: Monitor weight history and review performance

 <!-- ## 📹 Quick Demo -->

<!-- TODO: Add demo video -->

## 🛠️ Tech Stack & Architecture
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E) ![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white) ![CSS](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Next.js](https://img.shields.io/badge/next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white) ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white) ![Nginx](https://img.shields.io/badge/Nginx-009639?logo=nginx&logoColor=white&style=for-the-badge) ![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white) ![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white) 

BiteRight uses a containerized architecture with a Spring Boot REST API and a Next.js frontend, run using Docker and served through an Nginx reverse proxy.

### Backend (Spring Boot)
- **Security & Authorization**: Uses a stateless security model with **Spring Security** and **JSON Web Tokens (JWT)**. A custom `JwtFilter` checks bearer tokens against the user database for secure, token-based sessions without server-side storage.
- **Email Service**: Uses **SMTP via JavaMailSender** for sending automated emails (registration, password recovery). It uses HTML templates and a dedicated service layer.
- **Persistence Layer**: Built on **Spring Data JPA** with a MySQL database. It uses audit tables for weight and limit history to keep data consistent across the system.

### Frontend (Next.js)
- **Structure**: Built with the **Next.js App Router (React Server Components)**. It uses nested layouts to keep dashboard modules (meals, exercises, progress) separate and fast.
- **Data Management**: Uses a central API layer to talk to the backend. It manages state transitions with React hooks and service modules to keep data in sync across the frontend.

### Infrastructure & DevOps
- **Docker**: Managed by **Docker Compose**, with multi-stage builds for both the Spring Boot JAR and the Next.js production files.
- **Reverse Proxy**: **Nginx** handles request routing, static files, and CORS settings, acting as the main entry point for the application.

## 💾 Database & Data Model
The system relies on a relational database (MySQL) designed with 18 tables to manage user profiles, nutrition data, and historical records.
- **Triggers & Automation**: Database functions handle automatic BMI calculation, calorie burns, and history archiving to keep data synchronized.
- **Optimized Views**: SQL views (like `daily_summary`) aggregate complex data from multiple sources (meals, water, exercises) to simplify backend operations.
- **Consistency**: Strong relational constraints ensure data integrity across ingredients, recipes, and user logs.

## 🚀 Run Locally
Getting started with BiteRight is simple and straightforward. Follow these steps to get the application running on your local machine in just a few minutes.

First, download the project source code to your local machine:

```bash
git clone https://github.com/hbiegacz/BiteRight.git
```

Move into the project folder:
```bash
cd BiteRight
```

Start the entire application stack with a single command:
```bash
docker-compose up --build
```

Stop and clean the environment:
```bash
docker compose down -v
```

***Thanks for reading this far!***