Telekocsi (Carpool App)
=======================

A simple Spring Boot web app for sharing rides. Drivers post trips, passengers join them. Built with Spring Boot, Thymeleaf, and Bootstrap.

What it actually does
---------------------
* For Everyone:
  - Search for rides by start/end location and date.
  - See ride details like the driver, time, and how many seats are left.

* For Users:
  - Post a Ride: Drivers can list trips with origin, destination, time, and seat count.
  - Join/Leave: Passengers can join or leave rides with one click.
  - Logic: Drivers cannot join their own rides.

* For Admins:
  - Manage users and rides directly.
  - Safety Check: Hardcoded protection so you cannot accidentally delete the main 'admin' account.

The Stack
---------
* Backend: Java 17+, Spring Boot 3 (Web, Security, Data JPA).
* Frontend: Thymeleaf + Bootstrap 5 (dark mode style).
* Database: MySQL.
* Build: Maven.

How to run it
-------------

1. Prerequisites
   You need Java 17+ and Maven installed.
   You need a MySQL server running on localhost:3306.

2. Grab the code
   git clone https://github.com/Mirkoaml/telekocsi.git
   cd telekocsi

3. Database Setup
   The project is already configured for MySQL. Ensure your local MySQL server is running.
   
   Configuration (found in src/main/resources/application.properties):
   - Database: telekocsi_db (created automatically if missing)
   - Username: root
   - Password: 
   
   Update the 'spring.datasource.password' line in that file.

4. Run it
   mvn spring-boot:run

   Then head over to http://localhost:8080.

Login Info (Dev Data)
---------------------
If your database is empty, the app automatically creates these users on startup so you do not have to register manually:

* Role: Admin
  Username: admin
  Password: admin123
  Note: Can edit/delete anything.

* Role: Driver
  Username: sofor1
  Password: jelszo123
  Note: Has some sample rides posted.

* Role: Passenger
  Username: utas1
  Password: jelszo123
  Note: Just a regular user.

(You can always register a new user on the login page as well.)

License
-------
Free to use for whatever.
