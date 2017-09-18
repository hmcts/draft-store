# Draft store
The Draft store micro-service provides a ’save and resume’ feature for reform applications. 
It stores a draft json document against the userId provided.
 
## Getting Started

### Prerequisites
- [JDK 8](https://java.com)

### Building
To build the project execute the following command:
```bash
./gradlew build
```

### Running
Before you run the application you have to define `DRAFT_STORE_DB_HOST` and `DRAFT_STORE_DB_PASSWORD` environment
variables. Run the application by executing:
```bash
./gradlew bootRun
```

## API documentation
API documentation is provided with Swagger.  
You can view the json spec here: [http://localhost:8800/v2/api-docs](http://localhost:8800/v2/api-docs)  
Swagger UI is available here: [http://localhost:8800/swagger-ui.html](http://localhost:8800/swagger-ui.html)

## Developing

### Unit tests
To run all unit tests execute the following command:
```bash
./gradlew test
```

### Coding style tests
To run all checks (including unit tests) execute the following command:
```bash
./gradlew check
```

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details.
