
[![Contributors](https://img.shields.io/github/contributors/hbiegacz/BiteRight?color=red)](https://github.com/hbiegacz/BiteRight/graphs/contributors)
[![Commit Activity](https://img.shields.io/badge/Commits-📈%20View%20Graph-orange)](https://github.com/hbiegacz/BiteRight/graphs/commit-activity)
[![GitHub commits](https://badgen.net/github/commits/hbiegacz/BiteRight?color=yellow)](https://github.com/hbiegacz/BiteRight/commit/)
[![Repo Size](https://img.shields.io/github/repo-size/hbiegacz/BiteRight?color=green)](https://github.com/hbiegacz/BiteRight)
[![Lines of Code](https://img.shields.io/badge/Lines%20of%20Code-~250k-blue?logo=git)](https://github.com/hbiegacz/BiteRight)
[![License: MIT](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)

### 🚀 Meet the Development Team
**🖥️ Backend Development**
* 🔐 Hanna Biegacz \
    *Backend Developer & Database Architect*\
    Managed server-side authentication logic with JWT implementation and crafted the MySQL database schema

* 🐳 Maciej Cieślik \
    *Backend Developer & Docker Configuration Specialist* \
    Designed Docker containerization architecture, developed core API functionalities as well as application-wide testing

* ⚙️ Michał Iwanow-Kołakowski \
    *Backend Developer* \
    Built core API endpoints and server-side functionality

**🎨 Frontend Development**
* 🌟 Keira Kabongo-Barazzoli \
    *Frontend Developer* \
    Developed responsive web pages and user interface components 

* 💻 Anna Wierzbik \
    *Frontend Developer* \
    Architected the React frontend and connected it to Spring Boot APIs.


## Features
#### 🔐 User Authentication & Security 
- Secure user login and registration system with *JWT* token-based authentication
- Protected user profiles with encrypted data storage
- *Password recovery* system with secure email-based reset links
- *Email verification* for account activation and security confirmation
![login-page](/readme_images/image-1.png)

#### 📊 Smart Calorie Management
- *Daily Calorie Calculator:* Intelligent calorie limit calculation based on user goals and lifestyle
- *Meal Diary:* Comprehensive food logging with detailed ingredient tracking
- *Weight Progress Charts:* Visual weight change tracking over time with interactive graphs \
Meal tracking
![meal-tracking](/readme_images/image-3.png)
Healthy recipes for our users \
![example-recipes](/readme_images/image-4.png)
Visualising user progress \
![user_progress_statistics](/readme_images/image-5.png)

#### 🏃‍♂️ Exercise & Fitness Integration
- *Calorie Burn Calculator:* Accurate calorie expenditure tracking for various exercises
- *Balance Tracking:* Monitor calories consumed vs. calories burned

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

## 🛠️ Technologies
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E) ![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white) ![CSS](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Next.js](https://img.shields.io/badge/next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white) ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white) ![Nginx](https://img.shields.io/badge/Nginx-009639?logo=nginx&logoColor=white&style=for-the-badge) ![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)

##### 🖥️ **Backend**
- *Java* with *Spring Boot*, core of the backend application, API endpoints, application logic, database integration, and authentication services
- *MySQL*, relational database with Oracle Data Modeler used for schema design

##### 🎨 **Frontend**  
- *Next.js*, core of the frontend application
- *HTML5 & CSS3* for additional styling

##### 🔧 **DevOps & Version Control**
- *Docker* -  application containerization for consistent development
- *Ngix* 
- *Git* - version control   