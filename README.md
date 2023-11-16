# msa-practice

## Section2

### Load balancer 동작 확인  

| | discoveryservice | apigateway-service | first-service | second-service
------------|----------|----------|------------|----
Java        | 11       | 11       | 11         | 11
Spring Boot | 2.7.17   | 2.7.17   | 2.3.8      | 2.3.8
Spring Cloud| 2021.0.8 | 2021.0.8 | Hoxton.SR9 | Hoxton.SR9
Gradle      | 8.4      | 8.4      | 6.5        | 6.5

#### discoveryservice
- Discovery Service
- Eureka Server
#### apigateway-service
- API Gateway
- Eureka Discovery Client
#### first-service
- Microservice
- Eureka Discovery Client
#### second-service
- Microservice
- Eureka Discovery Client
