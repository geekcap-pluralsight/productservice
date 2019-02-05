# productservice

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=louisthomas_productservice&metric=alert_status)](https://sonarcloud.io/dashboard?id=louisthomas_productservice) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=louisthomas_productservice&metric=coverage)](https://sonarcloud.io/dashboard?id=louisthomas_productservice)
[![Known Vulnerabilities](https://snyk.io/test/github/louisthomas/productservice/badge.svg)](https://snyk.io/test/github/louisthomas/productservice)

Base on product-service from the TDD with Spring and Unit 5 PluralSight course.

* Use [Spring boot test](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html) best practices

### @SpringBooTest vs @WebMvcTest vs ...

When you use  @SpringBootTest, you are loading the complete application context. 
If you are only testing the controller and you're mocking the service, use @WebMvcTest. 

[Test auto-configuration annotations](https://docs.spring.io/spring-boot/docs/current/reference/html/test-auto-configuration.html)

Great [article](https://thepracticaldeveloper.com/2017/07/31/guide-spring-boot-controller-tests/) to explain the difference between test @nnotation

### Todos:
- [ ] Add integration test with [test containers](https://github.com/testcontainers/testcontainers-java)
- [ ] Add flyway database migration tool
- [X] Add Snyk dependencies security scan
- [X] Add dependabot automated dependency updates
- [ ] Add deployment to Pivotal Web service

