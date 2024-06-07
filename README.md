# All-in-one platform for booking events and sharing photos.

## Contents

1. [Description](#description)
2. [Features](#features)
3. [Built with](#built-with)
4. [API Documentation](#api-documentation)
   - [Artists](#artists)
   - [Comments](#comments)
   - [Coupons](#coupons)
   - [Events](#events)
   - [Orders](#orders)
   - [Posts](#posts)
   - [Support Tickets](#support-tickets)
   - [Support Ticket Replies](#support-ticket-replies)
   - [Tickets](#tickets)
   - [Users](#users)
   - [Cart Items](#cart-items)
   - [Authentication](#authentication)
5. [Installation](#installation)
6. [Starting The App](#starting-the-app)

<a name="description"></a>

## **Event Information System**

The Event Information System is a web application designed to facilitate the management and organization of events. Whether you're hosting a conference, seminar, workshop, or any other type of event, this system provides the tools necessary to streamline the process and ensure a successful outcome.

## **Features**

<a name="features"></a>

- <b>Event Creation</b>: Create new events with detailed information such as title, description, date, time, location, banner, participating artists and more.
- <b>Admin Dashboard</b>: An intuitive dashboard for administrators to oversee all events, users, support tickets and many more. Includes a rich panel with extensive statistics regarding every event - participation percentage, tickets sold for a period of time, number of successful orders.
- <b>Email Notifications</b>: Automated email notifications after successful orders/submitted support tickets or replies from the administration department.
- <b>Feedback</b>: Option to send support tickets regarding an issue during navigating the platform or just leaving a review.
- <b>Photo Sharing</b>: Built-in section allowing the users to post images from the events, as well as to comment other users' publications.

## **Built with**

<a name="built-with"></a>

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Spring](https://img.shields.io/badge/Spring-6db33f?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-ffffff?style=for-the-badge&logo=mysql&logoColor=black)](https://www.mysql.com/)
[![React.js](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)](https://reactjs.org/)
[![Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)](https://maven.apache.org)

## API Documentation

<a name="api-documentation"></a>

### Artists

<a name="artists"></a>

| Endpoint                            | Method | Description                                               |
|-------------------------------------|--------|-----------------------------------------------------------|
| `/artists`                          | GET    | Retrieve all artists.                                     |
| `/artists/profile-picture/{id}`     | GET    | Retrieve profile picture of a specific artist by ID.       |
| `/artists`                          | POST   | Add a new artist with profile picture.                    |
| `/artists/{id}`                     | PUT    | Update details of an existing artist.                     |
| `/artists/{id}`                     | DELETE | Delete an artist by ID.                                   |

### Comments

<a name="comments"></a>

| Endpoint                     | Method | Description                                    |
|------------------------------|--------|------------------------------------------------|
| `/comments/{username}`       | GET    | Retrieve all comments by a specific user.      |
| `/comments/{id}/is-read`    | PATCH  | Mark a comment as read by ID.                  |
| `/comments/{id}/is-removed` | PATCH  | Mark a comment as removed by ID.               |
| `/comments/{username}/{postId}` | POST | Add a new comment to a post by username.       |
| `/comments/{id}`            | DELETE | Delete a comment by ID.                        |

### Coupons

<a name="coupons"></a>

| Endpoint                   | Method | Description                                         |
|----------------------------|--------|-----------------------------------------------------|
| `/coupons`                 | GET    | Retrieve all coupons.                               |
| `/coupons/single-use`     | POST   | Generate single-use coupons.                        |
| `/coupons/{id}`            | DELETE | Delete a coupon by ID.                              |

### Events

<a name="events"></a>

| Endpoint                                   | Method | Description                                               |
|--------------------------------------------|--------|-----------------------------------------------------------|
| `/events/event-picture/{id}`               | GET    | Retrieve event picture by ID.                             |
| `/events/{id}`                             | GET    | Retrieve event details by ID.                             |
| `/events`                                  | GET    | Retrieve all events. Supports optional `type` query parameter. |
| `/events/active`                           | GET    | Get number of active events.                              |
| `/events/upcoming?type={type}`             | GET    | Get number of upcoming events by type.                    |
| `/events/booked?type={type}`               | GET    | Get number of booked events in the past by type.          |
| `/events/inactive`                         | GET    | Get number of inactive events.                            |
| `/events/{id}/attendance`                  | GET    | Get attendance percentage for event by ID.                |
| `/events`                                  | POST   | Add a new event with event picture.                       |
| `/events/{id}`                             | PUT    | Update details of an existing event with event picture.   |
| `/events/activity-status/{id}`             | PATCH  | Toggle event activity status by ID.                       |
| `/events/{id}`                             | DELETE | Delete an event by ID.                                    |

### Orders

<a name="orders"></a>

| Endpoint                            | Method | Description                             |
|-------------------------------------|--------|-----------------------------------------|
| `/orders/{id}`                      | GET    | Get order by ID.                        |
| `/orders/all/{username}`            | GET    | Get all orders for a user.              |
| `/orders/{username}`                | POST   | Create order for a user.                |
| `/orders/prices/last/{ordersCount}`| GET    | Get prices of last thirty orders.       |

### Posts

<a name="posts"></a>

| Endpoint                                  | Method | Description                                        |
|-------------------------------------------|--------|----------------------------------------------------|
| `/posts`                                  | GET    | Get all posts.                                     |
| `/posts/{username}/comments`              | GET    | Get all comments under user's posts.               |
| `/posts/{id}/picture`                     | GET    | Get post picture by ID.                            |
| `/posts/{username}`                       | POST   | Add a new post with post picture for a user.       |
| `/posts/{id}/{username}`                  | DELETE | Delete owned post by ID for a user.                |

### Support Tickets

<a name="support-tickets"></a>

| Endpoint                                    | Method | Description                                              |
|---------------------------------------------|--------|----------------------------------------------------------|
| `/support-tickets`                          | GET    | Retrieve all support tickets.                            |
| `/support-tickets/{username}`               | GET    | Retrieve all support tickets for a user by username.     |
| `/support-tickets/{username}`               | POST   | Create a support ticket for a user by username.          |
| `/support-tickets/{id}`                     | DELETE | Delete a support ticket by ID.                           |

### Support Ticket Replies

<a name="support-ticket-replies"></a>

| Endpoint                                    | Method | Description                                              |
|---------------------------------------------|--------|----------------------------------------------------------|
| `/support-ticket-replies/{username}`        | POST   | Reply to a support ticket for a user by username.        |

### Tickets

<a name="tickets"></a>

| Endpoint                                    | Method | Description                                              |
|---------------------------------------------|--------|----------------------------------------------------------|
| `/tickets/verification/{ticketCode}`        | GET    | Verify a ticket by ticket code.                          |
| `/tickets/sold-per-day-in-last-days/{days}`| GET    | Get sold tickets count per day for the last `days`.      |

### Users

<a name="users"></a>

| Endpoint                                                 | Method | Description                                              |
|----------------------------------------------------------|--------|----------------------------------------------------------|
| `/users/confirmation`                                    | GET    | Confirm token for user registration.                     |
| `/users`                                                 | GET    | Retrieve all users.                                      |
| `/users/{username}`                                      | GET    | Retrieve a user by username.                             |
| `/users/profile-picture/{username}`                      | GET    | Retrieve user profile picture by username.               |
| `/users`                                                 | POST   | Register a new user.                                     |
| `/users/password/{username}`                            | PATCH  | Change user password by username.                        |
| `/users/profile-picture/default/{username}`              | PATCH  | Reset user profile picture to default by username.       |
| `/users/{username}`                                      | PUT    | Update user details by username.                         |
| `/users/{username}/enabled`                             | PATCH  | Toggle user enabled status by username.                  |
| `/users/{username}/locked`                              | PATCH  | Toggle user locked status by username.                   |
| `/users/{username}`                                      | DELETE | Delete a user by username.                               |

### Cart Items

<a name="cart-items"></a>

| Endpoint                                    | Method | Description                                              |
|---------------------------------------------|--------|----------------------------------------------------------|
| `/cart/coupon/{username}`                   | GET    | Apply coupon for a user's cart items by username.                     |
| `/cart/number/{username}`                   | GET    | Get all cart items number for a user by username.        |
| `/cart/{username}`                         | GET    | Get all cart items for a user by username.              |
| `/cart`                                    | POST   | Add a cart item.                                         |
| `/cart/decrease`                           | POST   | Decrease cart item ticket quantity.                      |
| `/cart/{id}`                               | DELETE | Remove a cart item by ID.

## Authentication

<a name="authentication"></a>

| Endpoint              | Method | Description                                 |
|-----------------------|--------|---------------------------------------------|
| `/auth/authenticate`  | POST   | Authenticates a user.                      |


## Installation

<a name="installation"></a>

To set up the Event Information System project, follow these steps:

1. **MySQL Server 8.0.13**: You need to have installed a MySQL server 8.0.13. You can download and install it from [MySQL Official Website](https://dev.mysql.com/downloads/mysql/8.0.html).

2. **Java JDK 17**: Install Java JDK 17. You can download it from the [Oracle Java website](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html).

3. **Apache Maven**: Install Apache Maven from the official Apache Maven website. You can download it from [here](https://maven.apache.org/download.cgi).

4. **Clone the Repository**: Clone the repository to your local machine using the following command:

    ```
    git clone https://github.com/your-username/event-information-system.git
    ```

5. **Backend Setup**: Navigate to the `event-information-system-backend` directory and install the Maven packages by running the following command in the terminal:

    ```
    mvn clean install
    ```

6. **Frontend Setup**: Navigate to the `event-information-system-frontend` directory and install the npm packages by running the following command in the terminal:

    ```
    npm install
    ```

7. **Database Configuration**: Configure the MySQL database connection in the `application.properties` file located in the `event-information-system-backend/src/main/resources` directory. Update the database URL, username, and password according to your MySQL configuration.

8. **Run the Application**: Once the setup is complete, you can run the application. Start the backend server by running the Spring Boot application, and start the frontend server by running the React application.

## Starting the App

<a name="starting-the-app"></a>

1. **Start MySQL Server**: Ensure that your MySQL server is up and running.

2. **Double-Check Application Configuration Properties**: In the `application.properties` file located in the backend project, replace `myusername` and `mypassword` with your MySQL server username and password respectively.

3. **Start the Backend**:
    - Navigate to the `event-information-system-backend` folder.
    - Run the following command in the command prompt or terminal:
      ```
      mvn spring-boot:run
      ```
      
4. **Access the Application**: Once the backend server is running, you can access the application by navigating to the specified API endpoints.


