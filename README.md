# Shoppy API

## Introduction
Shoppy is a very simple API designed to handle:
- Products management 
- Checkout and order delivery
- Checkout management
- Many other things.

Take in consideration Shoppy is designed as a demo, it is not recommended to use
it in production environment.

## Purpose
This project is made for educational purposes in my path of understanding how web servers works on Java. Take in
consideration this project was not designed for production.

## Deployment
### Requirements
- MySQL Database
- Keycloak

### Set up
1. Set up your MySQL server and add its url and credentials to the application.properties file.
2. Set up your Keycloak server, create a realm called "shoppy" and add a client named "shoppy-api", configure it as a
bear-only client, then add roles USER and ADMIN.
3. Generate a client secret (from shoppy-api) and add it to the application.properties.

Once everything has been set up, then you can use Maven to run this project.
```shell
./mvnw build:run
```

## How to contribute
You can contribute via pull requests, some extra help is always appreciate.