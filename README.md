# Draft store
The Draft store micro-service provides a ’save and resume’ feature for reform applications. 
It stores a draft json document against the userId provided.
 
## Getting Started

Draft store is a SpringBoot application.
* Postgres is used to persist the documents.
* [Flyway](https://flywaydb.org/documentation/command/migrate) is used for data migration. Migration scripts are [here](./src/main/resources/db/migration). 
* gradle is the build tool 

## Environment Specific Properties
Currently default datasource properties are provided via [application.yaml](./application.yaml). 
These will have to be environment specific. 
The following properties will have to be supplied at runtime (unless using the defaults):

| Property                   | Default value                               |
| ---                        | ---                                         |
| DRAFT_STORE_DB_HOST        | localhost                                   |
| DRAFT_STORE_DB_PASSWORD    | draftstore                                  |


## API documentation
API documentation is provided with Swagger.
After starting the service go to [http://localhost:8800/v2/api-docs](http://localhost:8800/v2/api-docs)
