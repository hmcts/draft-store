[![Build Status](https://travis-ci.org/hmcts/draft-store.svg?branch=master)](https://travis-ci.org/hmcts/draft-store)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/35eb37f39906421387cfd120c35a538d)](https://www.codacy.com/app/HMCTS/draft-store)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/35eb37f39906421387cfd120c35a538d)](https://www.codacy.com/app/HMCTS/draft-store)

# Draft store
The Draft store micro-service provides a ’save and resume’ feature for reform applications via an internal API. 
It stores a draft json document against the userId provided.

![Low Level Design](/docs/design.png)

## Encryption feature in v3
A new version of the API has been created to meet [point 6 of the NCSC Security Design Principles: Reducing the impact 
of compromise](https://www.ncsc.gov.uk/guidance/design-principles-reducing-impact-compromise). 
The current API allows for both client to _optionally_ supply an encryption key. This optionality will be revoked in the 
future to ensure all partially completed form data being stored by service teams is encrypted per user.

For this reason, version 2 of the draft-store API is also now deprecated and will be removed on 31st January 2018. 
 
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

### Functional tests
To run all functional tests execute the following command:
```bash
./gradlew functionalTest
```

### Coding style tests
To run all checks (including unit tests) execute the following command:
```bash
./gradlew check
```

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details.
