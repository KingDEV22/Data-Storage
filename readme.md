# Data Collection System

## Overview
This project is designed to create a Data Collection System with the following features:

1. **User Authentication**: Users can register and log in using JWT-based authentication for secure access.

2. **Form Management**: Users can create, view, update, and delete forms as needed.

3. **Data Validation**: The system validates submitted form data to ensure accuracy and consistency.

4. **File Data Handling**: It allows users to upload and store data from files, such as CSV files.

5. **File Management**: Users can view, delete, and update data from uploaded files.

6. **Data Export**: The system supports the export of stored data from the database in CSV format, enabling users to download the collected data.

The architecture of this system is designed as a set of microservices, where each microservice is responsible for a specific functionality or component, promoting modularity and scalability. 

These services are:

1. **Organization Service:** This service enables organizations to register and utilize the platform for creating data collection forms and storing file-based data.

2. **Validator Service:** Hosted by this service, forms are validated to ensure that the data provided adheres to predefined constraints. Subsequently, the validated data is sent to a message queue for storage in the database.

4. **SMS/Email Service:** This service is responsible for sending success emails to users who have contributed data through the platform.

## Tools Used

1. Java
2. Typescript
3. Spring Boot
4. Node Js
5. Docker
6. RabbitMQ
7. Caching

## Setup and Run the project

1. Install ```Docker``` in your machine.
2. Open terminal run ```docker-compose build``` to create the docker images.
3. run ```docker-compose up``` to start all the servers.
4. Check the docker containers for the ports.

More features to be added in the future.....
