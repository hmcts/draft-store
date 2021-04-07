[![Build Status](https://travis-ci.com/hmcts/draft-store.svg?branch=master)](https://travis-ci.com/hmcts/draft-store)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/35eb37f39906421387cfd120c35a538d)](https://www.codacy.com/app/HMCTS/draft-store)
[![codecov](https://codecov.io/gh/hmcts/draft-store/branch/master/graph/badge.svg)](https://codecov.io/gh/hmcts/draft-store)

# Draft store
The Draft store micro-service provides a ’save and resume’ feature for reform applications via an internal API. 
It stores a draft json document against the userId provided.

![Low Level Design](/doc/design.png)

## Getting Started

### Prerequisites
- [JDK 8](https://java.com)

### Building
To build the project execute the following command:
```bash
./gradlew build
```
      
### Running
Before you run the application you have to define database connection.  
You can do this be either setting environment variables or creating `application-default.yaml` file.  
Run the application by executing:
```bash
./gradlew bootRun
```

### Consuming this service locally
This image is available in the HMCTS azure container registry (currently private)  
Image url is: `hmcts.azurecr.io/hmcts/draft-store-service`  
See required config in: [docker-compose.yml](docker-compose.yml)

### Operational note
When operating this service with live data, no one should under any circumstances view the encrypted data. 
The reason for this is because it would be considered under [Article 14 of GDPR](https://gdpr-info.eu/art-14-gdpr/) that we had commenced processing data. 
Then we would have thirty days to notify any third party parties mentioned in the application that we had commenced processing their data. 
This must not happen as the citizen, who is creating the application, has not yet submitted their application.

## API documentation
API documentation is provided with Swagger.  
You can view the json spec here: [http://localhost:8800/v2/api-docs](http://localhost:8800/v2/api-docs)  
Swagger UI is available here: [http://localhost:8800/swagger-ui.html](http://localhost:8800/swagger-ui.html)  
Updates to master documentation are reflected here: [https://hmcts.github.io/reform-api-docs/swagger.html](https://hmcts.github.io/reform-api-docs/swagger.html?url=https://hmcts.github.io/reform-api-docs/specs/draft-store.json)

## Developing

### Unit tests
To run all unit tests execute the following command:
```bash
./gradlew test
```

### Integration tests
To run all integration tests execute the following command:
```bash
./gradlew integration
```

### Code quality checks
We use [checkstyle](http://checkstyle.sourceforge.net/) and [PMD](https://pmd.github.io/).  
To run all checks execute the following command:
```bash
./lint.sh
```

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details.
