**Hi!**

**This is:** resource management and accounting application for those organisations who sell services and products by the subsctiption.

**It allows** to do the following:
  - Account schools, teachers, contracts, clients, event types (subjects, type of services or products), events.
  - Calculate return and teachers salaries.
  - Schedules for teachers and contracts.
  - Automatic event planning.
  - Nice visual calendar to see teacher's schedule.
  - Quickly calculate statistics to know the most or least profitable teacher, client, school, event type.

***
Architecture is in transit from bad MVC to good MVC.

I'm also working on documentation and adding comments.

At start it was very simple application and controller logic had been hosted inside the pages. Now all new logic is hosted inside Mediators (tap.execounting.dal.mediators) and their interfaces. Pages should only fire actions on this mediators. However the view logic of the pages is frequently not hosted inside mediators.
Do this -- and this will be clean MVC application.

**CORE**

At the core of the app are the list of entities:
 Event -- represents a lesson, or other training or learning session.
 Facility -- school or any other facility that could host Events. Facilities have Rooms for Events
 Room -- a room in a Facility, where Event is hosted.
 Teacher -- represents man who conducts Events with Clients
 Client -- represents a man who conducts

**The demo:**

To see it in action, after you have downloaded the package, you need to do three things:
 1. Import DemoData to your MySQL server.
  * Note that the app is configured to access mysql service on 3306, as root identified by 123258789. This could be modified at "src/main/resources/hibernate.cfg.xml"
 2. Build Gradle script (please be familiar with Gradle).
  * just type "gradle jettyRun" in the root folder of the app
 3. Open localhost:8080/bureau in the browser, login as root identified by "password".

Prerequisites:
 1. MySQL is server is up and running on 3306.
 2. JRE 7 or higher is installed and JAVA_HOME is set up.
 3. Gradle is installed, and GRADLE_HOME is set up.
 4. java and gradle binaries are in the system PATH variable.

Give me feedbacks.