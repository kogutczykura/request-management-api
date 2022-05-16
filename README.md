## Application for processing request
Spring boot based application for managing request written in java. Data are persisted in relational database (PostgreSQL) and accessed using ORM framework Hibernate. 



### How to run

The easiest way to run this application is to install docker and run `docker-compose build && docker-compose up` command. This will run postgres database and the application.

### Rest endpoints

In order to see available rest endpoints access `http://localhost:8081` after running in docker.

### Shortcuts
- database schema is generated using hibernate. In production application flyway/liquibase should be used
- better way to store history changes would be to use event sourcing because current implementation require fetching all status changes along with request