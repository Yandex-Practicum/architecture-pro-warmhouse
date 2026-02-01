```puml
@startuml source

''' init layout

!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml

LAYOUT_WITH_LEGEND()
skinparam linetype ortho
skinparam nodesep 150
skinparam ranksep 100
skinparam sequenceMessageAlign center

<style>
arrow {
    FontName Arial
    FontSize 12
    FontStyle italic
    FontColor black
}
</style>

''' end init layout

title
    <b>Система управления отоплением</b>
    <i> Исходная схема приложения - монолит</i>
    <i> C2 - Components </i>
end title

Person(admin, "Administrator", "Manage system")
Person_Ext(user, "User", "User, who get sensor's data")

System_Boundary(sourceSystem, "Main system") {
    Container(server, "Server Instance", "golang", "One sensor -> one instance") {
        Component(mainApi, "external API", "gin", "CRUD")  
        Component(sensorHandler, "TemperatureService", "", "HTTP request to sensor")
        Component(dal, "DataLayer", "pgxpool", "Operations with DB")
        ComponentDb(env, "Settings storage", "env variables")

        Rel_D(mainApi, dal, "manage sensors")
        Rel_R(mainApi, sensorHandler, "Get current\nvalue")
        Rel_D(sensorHandler, env, "Get sensor\nAPI link")
        Rel_R(dal, env, "Get DB\nconnString")
    }

    SystemDb(db, "System Storage", "Store settings and statistics for all sensors")

    Rel_D(dal, db, "Get or Update\nconcrete sensor data")
}

System_Boundary(client, "Local temperature sensor") {
    System_Ext(sensor, "Temperature sensor"){
        Component_Ext(sensorApi, "Sensor API", "ASP.NET", "Manage concrete sensor")
        Component_Ext(sensorEmbedded, "Current sensor", "C/C++", "Low-Level operating sensor")

        Rel_D(sensorApi, sensorEmbedded, "Manage sensor", "InteropServices")
    }   
}

Rel_D(admin, mainApi, "Manage system", "HTTP")
Rel_D(user, mainApi, "Manage sensors", "HTTP")
Rel_R(sensorHandler, sensorApi, "Get current\nvalue", "HTTP")

@enduml
```