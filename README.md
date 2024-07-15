# User Management and Authentication Demo Application

This is a demonstration application for managing users and roles, handling authentication using Java 21, Spring Boot, Spring Security, and Java-Jwt. The application also includes an email notification service for login and password modification events.

## Features

- User Registration and Management
- Role-Based Access Control
- JWT-based Authentication
- Email Notifications for:
  - Successful Logins
  - Password Changes

## Technologies Used

- Java 21
- Spring Boot
- Spring Security
- Java-Jwt
- Spring Data JPA
- H2 Database (for development)
- Thymeleaf (for views)
- JavaMailSender (for email notifications)

## Getting Started

### Prerequisites

- JDK 21 or later
- Maven 3.6+
- An email server or SMTP service (for email notifications)

### Installation

1. **Clone the repository**
    ```bash
    git clone https://github.com/your-username/user-management-demo.git
    cd user-management-demo
    ```

2. **Configure application properties**

    Open `src/main/resources/application.properties` and set your email server configurations:
    ```properties
    spring.mail.host=smtp.example.com
    spring.mail.port=587
    spring.mail.username=your-email@example.com
    spring.mail.password=your-email-password
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true

    jwt.secret=your-jwt-secret-key
    ```

3. **Build and run the application**

    Use Maven to build and run the application:
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

4. **Access the application**

    Open your browser and navigate to `http://localhost:8888`.

## Endpoints

### Authentication

- **POST** `/api/auth/login` - Authenticate a user and return a JWT
- **POST** `/api/auth/register` - Register a new user

### User Management

- **GET** `/api/users` - Get a list of all users (Admin only)
- **GET** `/api/users/{id}` - Get details of a specific user
- **PUT** `/api/users/{id}` - Update user information
- **DELETE** `/api/users/{id}` - Delete a user

### Roles Management

- **GET** `/api/roles` - Get a list of all roles (Admin only)
- **POST** `/api/roles` - Create a new role (Admin only)
- **PUT** `/api/roles/{id}` - Update role information (Admin only)
- **DELETE** `/api/roles/{id}` - Delete a role (Admin only)

## Email Notifications

The application sends email notifications for the following events:

- **Login** - A notification is sent to the user's registered email after a successful login.
- **Password Change** - A notification is sent to the user's registered email after a password change.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Spring Boot Documentation
- Spring Security Documentation
- Java JWT Documentation

## Contact

For any issues or questions, please open an issue on GitHub or contact the repository owner at your-email@example.com.
