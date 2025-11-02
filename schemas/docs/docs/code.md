```puml
@startuml
title FitLife Context Diagram

top to bottom direction

!includeurl https://raw.githubusercontent.com/RicardoNiepel/C4-PlantUML/master/C4_Component.puml

Person(user, "User", "A user of the fitness club system")
Person(admin, "Administrator", "An administrator managing the system")
System(FitLifeSystem, "FitLife System", "System managing memberships, schedules, and payments")

System_Ext(api, "Third-Party API", "External API for fitness data integration", "Uses REST API, JSON data format")
System_Ext(bank, "Bank System", "External bank for processing payments", "Uses SOAP, XML data format")
Rel(user, FitLifeSystem, "Uses the system")
Rel(admin,FitLifeSystem,"Manages the system")
Rel(FitLifeSystem,api,"Fetches fitness data")
Rel(FitLifeSystem,bank,"Processes payments")

@enduml
```