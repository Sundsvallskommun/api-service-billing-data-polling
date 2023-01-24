# Billing DataPolling

## Leverantör

Sundsvalls kommun

## Beskrivning
BillingDataPolling är en tjänst som ansvarar för att hämta data från e-tjänsteflöden och sedan skicka dessa till BillingPreProcessor.

## Tekniska detaljer

### Starta tjänsten

|Miljövariabel|Beskrivning|
|---|---|
|**Databasinställningar**||
|`spring.datasource.url`|JDBC-URL för anslutning till databas|
|`spring.datasource.username`|Användarnamn för anslutning till databas|
|`spring.datasource.password`|Lösenord för anslutning till databas|
|`spring.jpa.properties.javax.persistence.schema-generation.database.action`|Action är default none, men bör ändras till önskat värde (tex update eller verify)|
|`spring.flyway.enabled`|Flyway är avslagen default, men kan slås på ifall versionshantering via Flyway önskas|

### Paketera och starta tjänsten
Applikationen kan paketeras genom:

```
./mvnw package
```
Kommandot skapar filen `api-service-billing-data-polling-<version>.jar` i katalogen `target`. Tjänsten kan nu köras genom kommandot `java -jar target/api-service-billing-data-polling-<version>.jar`. Observera att en lokal databas måste finnas startad för att tjänsten ska fungera.

### Bygga och starta med Docker
Exekvera följande kommando för att bygga en Docker-image:

```
docker build -f src/main/docker/Dockerfile -t api.sundsvall.se/ms-billing-data-polling:latest .
```

Exekvera följande kommando för att starta samma Docker-image i en container:

```
docker run -i --rm -p8080:8080 api.sundsvall.se/ms-billing-data-polling

```

#### Kör applikationen lokalt

Exekvera följande kommando för att bygga och starta en container i sandbox mode:  

```
docker-compose -f src/main/docker/docker-compose-sandbox.yaml build && docker-compose -f src/main/docker/docker-compose-sandbox.yaml up
```


## 
Copyright (c) 2022 Sundsvalls kommun