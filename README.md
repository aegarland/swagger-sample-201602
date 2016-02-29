This is a simple REST service that is annotated with swagger tags.  It was built with the modest goal of building an 
alternative example to the pet store for interacting with swagger-ui and borrows from other similar demos online (although
few demos bother to implement more than @GET).

It is maven build that produces a war that runs in tomcat8, using jersey 2 (with jackson) and Spring 4.  

Persistance layer right now is JPA/ in-memory HSQL which allows to easily do "realistic" test of all CRUD operations
in the swagger UI.  I suppose it could be the basis for doing front-end unit integration tests.
