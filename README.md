# Sample01
This is an in-progress project modeling a finctional 'Tuition Reimbursement Management System'.

It is design to be a web app with RESTful architecture. Technologies used:
- Javalin
- Log4j
- DataStax
- Amazon Keyspaces for Apache Cassnadra

Layers:
- Beans
- Data / Data Access Object
- Service
- Controller

Completed functionalities:
- Initialization of connection to database (Amazon Keyspaces) and basic system integrity.
- User login and logout (via HTTPS requests)
- User accound creation and deletion (via HTTPS requests)
- Assignment to a given User (of a certain type), appropriate references to Supervisors, or other authorized users.

Entities:
- User (generic)
- Admin (is a User)
- Employee / Emp (is a User)
- BenefitsCoordinator / BenCo (is a User)
- DirectSupervisor / DirSup (is a User)
- DepartmentHead / DeptHead (is a DirectSupervisor)
