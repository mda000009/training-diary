# training-diary

## Description
The goal of this REST API is to record gym training sessions.

## Installation
To run the project locally, the prerequisites detailed in the "Prerequisites" section must be met. Then open a terminal or command line in the project directory and execute the command: mvnw.cmd spring-boot:run

## Usage
Once the project is running locally, you can send requests to any API endpoint. You can use the Postman collections hosted at "WAITING_URL". You can also make requests to the endpoints via "https://editor.swagger.io/" using the file training-diary/src/main/resources/swagger/training-diary-v1.yml.  

## Contribution
To contribute to the project, please contact us via the email provided in the "Contact" section.  

## Contact
0610809824@uma.es

## Project Structure
training-diary/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── isia/
│   │   │           └── tfm/
│   │   │               ├── config
│   │   │               ├── controller
│   │   │               ├── entity
│   │   │               ├── exception
│   │   │               ├── repository
│   │   │               └── service/
│   │   │                   └── impl
│   │   ├── resources/
│   │   │   ├── config
│   │   │   └── swagger
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── isia/
│       │           └── tfm/
│       │               ├── controller
│       │               ├── exception
│       │               ├── service.impl
│       │               └── testutils
│       └── resources/
│           └── mocks
└── pom.xml

## Prerequisites
- Install JDK 17
- Configure the system environment variables DB_PASSWORD and MAIL_PASSWORD. To know their values, please contact us via the email provided in the "Contact" section.

## Other Considerations
- Although there are only three endpoints, three controllers have been created because the idea is to continue expanding the project and create the rest of the operations (GET, PUT, and DELETE) in each controller.
- Security has not been added to the API (JWT Token, OAuth 2.0...) because the project is purely educational and is not intended to be deployed on any remote server at the moment.