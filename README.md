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


### Endpoints
  
| Retrieve    |                                                                                                                                     |
| ---         | ---                                                                                                                                 |
| Description | retrieve a draft document of given type                                                                                             |
| URL         | /api/v2/draft/`<draftType>`                                                                                                         |
| Method      | GET                                                                                                                                 |
| Headers     | Authorization: `hmcts-id <userId>`                                                                                                  |
| Example     | `curl -X GET -H "Content-Type: application/json" -H "Authorization: hmcts-id 123546" "http://localhost:8800/api/v2/draft/petition"` |

| Save        |                                                                                                                                                                       |
| ---         | ---                                                                                                                                                                   |
| Description |  persist draft data of given type against a user                                                                                                                      |
| URL         | /api/v2/draft/`<draftType>`                                                                                                                                           |
| Method      | POST                                                                                                                                                                  |
| Headers     | Authorization: `hmcts-id <userId>`</br>Content-Type: `application/json`                                                                                               |
| Params      | draft data as json                                                                                                                                                    |
| Example     | `curl -X POST -H "Content-Type: application/json" -H "Authorization: hmcts-id 123546" -d '{"name": "Marcus Bamforth"}' "http://localhost:8800/api/v2/draft/petition"` |
  
| Delete      |                                                                                                    |
| ---         | ---                                                                                                |
| Description | delete draft data of given type                                                                    |
| URL         | /api/v2/draft/`<draftType>`                                                                        |
| Method      | DELETE                                                                                             |
| Headers     | Authorization: `hmcts-id <user-id>`                                                                |
| Example     | `curl -X DELETE -H "Authorization: hmcts-id 123546" "http://localhost:8800/api/v2/draft/petition"` |

### Deprecated endpoints
  
| Retrieve    |                                                                                                                            |
| ---         | ---                                                                                                                        |
| Description | retrieve a draft document                                                                                                  |
| URL         | /api/v1/draft                                                                                                              |
| Method      | GET                                                                                                                        |
| Headers     | Authorization: `hmcts-id <userId>`                                                                                         |
| Example     | `curl -X GET -H "Content-Type: application/json" -H "Authorization: hmcts-id 123546" "http://localhost:8800/api/v1/draft"` |

| Save        |                                                                                                                                                              |
| ---         | ---                                                                                                                                                          |
| Description |  persist draft data against a user id                                                                                                                        |
| URL         | /api/v1/draft                                                                                                                                                |
| Method      | POST                                                                                                                                                         |
| Headers     | Authorization: `hmcts-id <userId>`</br>Content-Type: `application/json`                                                                                      |
| Params      | draft data as json                                                                                                                                           |
| Example     | `curl -X POST -H "Content-Type: application/json" -H "Authorization: hmcts-id 123546" -d '{"name": "Marcus Bamforth"}' "http://localhost:8800/api/v1/draft"` |
  
| Delete      |                                                                                           |
| ---         | ---                                                                                       |
| Description | delete draft data                                                                         |
| URL         | /api/v1/draft                                                                             |
| Method      | DELETE                                                                                    |
| Headers     | Authorization: `hmcts-id <user-id>`                                                       |
| Example     | `curl -X DELETE -H "Authorization: hmcts-id 123546" "http://localhost:8800/api/v1/draft"` |

