# OneViewMed Backend Service Project

## Requirements

### Build

- [Java Development Kit (JDK)](https://adoptium.net/) 21+
- [Apache Maven](https://maven.apache.org/) 3.5+

### Run

- [Java Runtime Environment (JRE)](https://adoptium.net/) 21+
- (for local FHIR repo) [Docker Desktop](https://www.docker.com/products/docker-desktop/)
  4.26+

## Build and Test

```shell
mvn clean install
```

## Run local

1. Start FHIR repository

    ```shell
    docker-compose up
    ```

2. Start backend

    ```shell
    cd application
    mvn spring-boot:run -Dspring-boot.run.profiles=local
    ```

## Profiles

### Maven

- `coverage`
  Collects code coverage information during test execution
  via [Jacoco](https://www.eclemma.org/jacoco/)
- `static-analysis`
  Performs static code analysis
  with [Spotbugs](https://spotbugs.github.io/), [PMD](https://pmd.github.io/), [Checkstyle](https://checkstyle.sourceforge.io/)
  and [Forbidden API Checker](https://github.com/policeman-tools/forbidden-apis)

## API

### Service

To interact with the REST API, the service provides a Swagger UI
at [`http://localhost:8080/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html).

### FHIR Repository

In the root directory of this project there is a Docker Compose file with which
a [HAPI FHIR](http://hapifhir.io/) repository can be started.
The FHIR repository has a web interface with which functions of the FHIR API, such as the creation
of resources, can be carried out.
After starting, the web interface can be accessed
under [`http://localhost:8081/`](http://localhost:8081/).
