# MJ Training Camp - Gym Membership Management System

<div align="center">

![Status](https://img.shields.io/badge/Status-Active-success)
![License](https://img.shields.io/badge/License-MIT-blue)

🏋️ A comprehensive gym membership and class booking management system built with Spring Boot and Vue.js

[Live Demo](https://mj-training-camp.vercel.app/) | [Report Bug](../../issues) | [Request Feature](../../issues)

</div>

## 📋 Table of Contents

- [About The Project](#about-the-project)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## 🎯 About The Project

MJ Training Camp is a full-stack web application designed to streamline gym operations, membership management, and fitness class bookings. The system provides role-based access for members, trainers, and administrators, offering a seamless experience for managing gym activities.

### Key Highlights

- **Multi-Role System**: Separate dashboards and functionalities for Members, Trainers, and Administrators
- **Membership Management**: Easy purchase and management of gym memberships
- **Class Booking**: Browse and book fitness classes with real-time availability
- **Payment Processing**: Secure payment system with verification workflow
- **Real-Time Updates**: Live tracking of class schedules and member attendance
- **Responsive Design**: Mobile-friendly interface built with Vue.js and Bootstrap

## ✨ Features

### For Members

- 👤 User registration and authentication
- 💳 Browse and purchase membership packages
- 📅 View and book available fitness classes
- 📊 Track class history and attendance
- 💰 View transaction history
- 👥 Manage personal profile

### For Trainers

- 📋 View assigned classes and schedules
- ✅ Mark member attendance
- 📊 View class details and participants
- 👤 Manage personal profile

### For Administrators

- 📦 Create and manage membership packages
- 🏋️ Schedule and manage fitness classes
- 👨‍🏫 Add and manage trainers
- 👥 Oversee member registrations
- ✅ Verify payment transactions
- 📈 Dashboard with analytics and insights

## 🛠️ Tech Stack

### Backend

- **Framework**: Spring Boot 3.2.2
- **Language**: Java 21
- **Database**: PostgreSQL (Production) / MySQL (Development)
- **Security**: Spring Security with JWT Authentication
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven

### Frontend

- **Framework**: Vue.js 3
- **Router**: Vue Router 4
- **HTTP Client**: Axios
- **UI Framework**: Bootstrap 5
- **Charts**: ApexCharts
- **Icons**: Font Awesome
- **Build Tool**: Vite
- **Notifications**: SweetAlert2

### DevOps & Deployment

- **Frontend Hosting**: Vercel
- **Database**: Supabase (PostgreSQL)
- **SSL**: HTTPS enabled with PKCS12 keystore

## 🚀 Getting Started

### Prerequisites

Before running this project, make sure you have the following installed:

- **Java Development Kit (JDK) 21** or higher
- **Node.js 16** or higher
- **npm** or **yarn**
- **Maven 3.8+**
- **PostgreSQL** or **MySQL** database
- **Git**

### Installation

#### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/MJTrainingCamp.git
cd MJTrainingCamp
```

#### 2. Backend Setup

```bash
# Navigate to backend directory
cd backend

# Install dependencies (Maven will download them automatically)
mvn clean install

# Or use the Maven wrapper
./mvnw clean install    # Linux/Mac
mvnw.cmd clean install  # Windows
```

#### 3. Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Or using yarn
yarn install
```

### Configuration

#### Backend Configuration

1. Create or update `backend/src/main/resources/application.properties`:

```properties
# Database Configuration (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# Or MySQL Configuration
# spring.datasource.url=jdbc:mysql://localhost:3306/your_database
# spring.datasource.username=root
# spring.datasource.password=your_password
# spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.jdbc.time_zone=UTC

# Server Configuration
server.port=8443

# SSL Configuration (Optional for development)
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your_password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=mykey

# Logging
logging.level.org.hibernate.SQL=DEBUG
```

2. Create a `.env` file in the backend directory for sensitive data (optional):

```env
DB_URL=jdbc:postgresql://localhost:5432/your_database
DB_USERNAME=your_username
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key
```

#### Frontend Configuration

Create a `.env` file in the `frontend` directory:

```env
VITE_API_BASE_URL=https://localhost:8443/api
# Or for development without SSL
# VITE_API_BASE_URL=http://localhost:8080/api
```

### Running the Application

#### Start Backend

```bash
cd backend

# Using Maven
mvn spring-boot:run

# Or using Maven wrapper
./mvnw spring-boot:run    # Linux/Mac
mvnw.cmd spring-boot:run  # Windows
```

The backend will start at `https://localhost:8443` (or `http://localhost:8080` if SSL is disabled)

#### Start Frontend

```bash
cd frontend

# Development server
npm run dev

# Or using yarn
yarn dev
```

The frontend will start at `http://localhost:5173` (default Vite port)

#### Build for Production

**Frontend:**

```bash
cd frontend
npm run build
```

**Backend:**

```bash
cd backend
mvn clean package
java -jar target/bookMembership-0.0.1.jar
```

## 📖 Usage

### Default User Roles

After setting up the database, you'll need to create initial users with different roles:

1. **Admin**: Full system access
2. **Trainer**: Class management and attendance
3. **Member**: Book classes and manage memberships

### Accessing the Application

1. Navigate to `http://localhost:5173` (development) or your deployed URL
2. Register a new account or login with existing credentials
3. Based on your role, you'll be redirected to the appropriate dashboard

### Workflow Example

1. **Admin** creates membership packages and schedules classes
2. **Member** registers, purchases a membership, and books classes
3. **Admin** verifies the payment transaction
4. **Trainer** marks attendance when members attend classes
5. **Member** views class history and transaction records

## 📁 Project Structure

```
MJTrainingCamp/
├── backend/                          # Spring Boot Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/project/bookMembership/
│   │   │   │   ├── auth/            # Authentication & JWT
│   │   │   │   ├── classes/         # Class management
│   │   │   │   ├── config/          # Spring configuration
│   │   │   │   ├── DTO/             # Data Transfer Objects
│   │   │   │   ├── membership/      # Membership logic
│   │   │   │   ├── packageList/     # Package management
│   │   │   │   ├── trainer/         # Trainer management
│   │   │   │   ├── transaction/     # Payment transactions
│   │   │   │   ├── user/            # User management
│   │   │   │   └── visitPackage/    # Visit package logic
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                    # Unit tests
│   ├── pom.xml                      # Maven dependencies
│   └── Dockerfile                   # Docker configuration
│
├── frontend/                        # Vue.js Frontend
│   ├── src/
│   │   ├── components/
│   │   │   ├── Admin/              # Admin components
│   │   │   ├── Member/             # Member components
│   │   │   ├── Trainer/            # Trainer components
│   │   │   └── *.vue               # Shared components
│   │   ├── router/                 # Vue Router configuration
│   │   ├── assets/                 # CSS and static assets
│   │   ├── App.vue                 # Root component
│   │   └── main.js                 # Application entry point
│   ├── public/                     # Public assets
│   ├── package.json                # npm dependencies
│   └── vite.config.js              # Vite configuration
│
└── README.md                       # This file
```

## 📚 API Documentation

The backend provides RESTful APIs for all functionalities. Key endpoints include:

### Authentication

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout

### Membership Packages

- `GET /api/packages` - Get all packages
- `POST /api/packages` - Create new package (Admin)
- `PUT /api/packages/{id}` - Update package (Admin)
- `DELETE /api/packages/{id}` - Delete package (Admin)

### Classes

- `GET /api/classes` - Get all classes
- `GET /api/classes/{id}` - Get class details
- `POST /api/classes` - Create class (Admin)
- `PUT /api/classes/{id}` - Update class (Admin)
- `POST /api/classes/{id}/book` - Book class (Member)

### Transactions

- `GET /api/transactions` - Get user transactions
- `POST /api/transactions` - Create transaction
- `PUT /api/transactions/{id}/verify` - Verify payment (Admin)

_For complete API documentation, refer to the backend `Api docs/` directory._

## 🤝 Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

Distributed under the MIT License. See `LICENSE` file for more information.

## 📞 Contact

Project Link: [https://github.com/yourusername/MJTrainingCamp](https://github.com/yourusername/MJTrainingCamp)

Live Demo: [https://mj-training-camp.vercel.app/](https://mj-training-camp.vercel.app/)

---

<div align="center">
Made with ❤️ by MJ Training Camp Team
</div>
