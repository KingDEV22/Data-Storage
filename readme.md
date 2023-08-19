# Service Description: Data Collection System

## Overview
This project implements a Data Collection System enabling organizations to register, create forms, collect data, and validate the form answers. The system also includes features to store data in a database and send success responses to users through email.

The system is designed as a set of microservices, each responsible for a specific functionality. These services are:

1. **Organization Service:** This service allows new organizations to register and use the service to create form for data collection.

2. **Validator Service:** The Validator Service is a server to host form and validate data in the form answers based on predefined constraints. It then send the data to message queue to save it in the database.

4. **SMS/Email Service:** This service handles sending success responses to users from whom data is collected.


## Tools Used

1. Java
2. Typescript
3. Spring Boot
4. Node Js
5. Docker
6. RabbitMQ
7. Redis

## Setup and Run the project

1. Install ```Docker``` in your machine.
2. Open terminal run ```docker-compose build``` to create the docker images.
3. run ```docker-compose up``` to start all the servers.
4. Check the docker containers for the ports.


Still in development...
