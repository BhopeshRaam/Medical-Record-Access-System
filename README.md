# Medical Record Access System (MRAS)

Medical Record Access System (MRAS) is a secure, role-based healthcare
management platform designed to efficiently manage and control access to
patient medical records. The system ensures that sensitive healthcare
data is protected while remaining accessible to authorized personnel
such as administrators, doctors, nurses, receptionists, and patients.
MRAS is built with scalability, security, and clean architecture
principles in mind.

The primary objective of MRAS is to digitize and streamline medical
record management in healthcare institutions. The system supports secure
authentication using JWT-based authorization, structured data storage
using MongoDB, and a responsive user interface built with React. By
implementing strict role-based access control, the platform ensures that
users can only access data relevant to their responsibilities, thereby
maintaining privacy and compliance standards.

MRAS is designed to simulate real-world hospital workflows including
patient registration, medical record updates, diagnosis tracking, and
secure document handling. The project demonstrates backend API
development, database schema design, secure authentication mechanisms,
and modern frontend integration. It reflects practical knowledge of
enterprise-level application development using Spring Boot and MongoDB
with a decoupled React frontend.

------------------------------------------------------------------------

Tech Stack

Backend: - Java Spring Boot - Spring Security (JWT Authentication) -
Spring Data MongoDB - RESTful APIs - Lombok - Validation API - OpenAPI
(Swagger)

Database: - MongoDB Atlas - GridFS for file storage (medical reports,
scans, documents)

Frontend: - React.js - Tailwind CSS - Axios - React Router

------------------------------------------------------------------------

Core Features

-   Role-Based Access Control (ADMIN, DOCTOR, NURSE, RECEPTIONIST,
    PATIENT)
-   Secure JWT Authentication and Authorization
-   Patient Record Creation and Management
-   Diagnosis and Treatment Tracking
-   File Upload and Retrieval for Medical Reports
-   RESTful API Design
-   Responsive User Interface
-   Structured and Modular Backend Architecture

------------------------------------------------------------------------

System Architecture

Client (React Frontend) \| v Spring Boot REST API (JWT Secured) \| v
MongoDB Database

Authentication Flow: 1. User logs in with credentials. 2. Backend
validates credentials and generates JWT token. 3. Token is sent in the
Authorization header for subsequent requests. 4. Backend verifies token
and grants access based on role permissions.

------------------------------------------------------------------------

Project Structure

MRAS │ ├── backend │ ├── controller │ ├── service │ ├── repository │ ├──
model │ └── config │ └── frontend ├── components ├── pages ├── api └──
assets

------------------------------------------------------------------------

Setup Instructions

Backend Setup: 1. Clone the repository. 2. Configure MongoDB URI in
application.properties. 3. Run the Spring Boot application using:

mvn spring-boot:run

Backend runs at: http://localhost:8080

Frontend Setup: 1. Navigate to frontend directory. 2. Install
dependencies:

npm install

3.  Start development server:

    npm start

Frontend runs at: http://localhost:3000

------------------------------------------------------------------------

API Documentation

Swagger UI is available at: http://localhost:8080/swagger-ui.html

------------------------------------------------------------------------

Project Objective

The objective of MRAS is to demonstrate the implementation of a secure,
scalable, and maintainable medical record management system using modern
full-stack technologies. The system emphasizes secure data handling,
efficient backend architecture, REST API development, and seamless
frontend-backend integration. It reflects strong understanding of
authentication mechanisms, database modeling, modular system design, and
real-world healthcare workflow simulation.

------------------------------------------------------------------------

Developed By

Bhopesh Raam
