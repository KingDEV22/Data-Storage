# Service Description: Data Collection System

## Overview
![design](https://github.com/KingDEV22/Data-Storage/assets/98249720/a3f20a98-1fd1-4472-93ea-2db96f117b25)

This project implements a Data Collection System enabling organizations to register, create forms, collect data, and validate the form answers. The system also includes features to store data in a database and send success responses to users through SMS.

The system is designed as a set of microservices, each responsible for a specific functionality. These services are:

1. **Organization Service:** This service allows new organizations to register and use the system for data collection. Organizations can create forms that will be used to collect data from users.

2. **Validator Service:** The Validator Service is implemented as a simple AWS Lambda service. Its purpose is to validate text in the form answers based on predefined constraints. This service ensures that the data submitted by users is error free.

3. **Database Service:** The Database Service is responsible for storing the collected data in the database. It ensures secure storage and retrieval of data submitted through the forms.

4. **SMS/Email Service:** This is AWS Lambda service that handles sending success responses to users from whom data is collected.
