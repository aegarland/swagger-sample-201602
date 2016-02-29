This is a simple REST service that is annotated with https://github.com/swagger-api tags.
  
It was built with the modest goal of building an alternative example to the pet store for interacting with  https://github.com/swagger-api/swagger-ui and borrows from other similar demos online (although few demos bother to implement more than @GET).

* It is maven build that produces a war that runs with:
  * Tomcat8
  * Jersey 2
  * Jackson
  * Spring 4  

* Persistance layer right now is
   * JPA via eclipselink with Local transactions
   * In-memory HSQL

This allows to easily do "realistic" test of all CRUD operations in the swagger UI.  I suppose it could be the basis for doing front-end unit integration tests.
