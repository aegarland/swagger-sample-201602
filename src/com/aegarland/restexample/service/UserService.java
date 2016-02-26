package com.aegarland.restexample.service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;

import com.aegarland.restexample.entity.User;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@Path("/user")
@Api(value = "/user", description = "CRUD operations for users")
public class UserService {

	private JsonFactory factory = new JsonFactory();

	{
		factory.enable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
		factory.enable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
		factory.setCodec(new com.fasterxml.jackson.databind.ObjectMapper(factory));
	}

	EntityManagerFactory emf = Persistence.createEntityManagerFactory("rest-example-hsql");

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "return a list of all users", notes = "too many results")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful conversion", response = User[].class) })
	public Response getUsers() throws JSONException, IOException {
		Writer w = new StringWriter();
		User[] users = new User[0];
		try {
			EntityManager em = emf.createEntityManager();
		    TypedQuery<User> typedQuery = em.createQuery("SELECT u FROM User u", User.class);
		    List<User> list = typedQuery.getResultList();
		    users = list.toArray(users);
		} catch (IllegalArgumentException e) {
			// log this or take some other more sensible action
		}
		factory.createGenerator(w).writeObject(users);

		return Response.ok(w.toString()).build();
	}

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "return user object based on passed in user id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful insert", response = User.class),
			@ApiResponse(code = 404, message = "User not found") })
	public Response getUser(@ApiParam(name = "id", value = "user id", required = true) @PathParam("id") long id)
			throws JSONException, IOException {

		User user = null;

		try {
			EntityManager em = emf.createEntityManager();
			user = em.find(User.class, id);
		} catch (IllegalArgumentException e) {
			// TODO log this or take some other more sensible action
		}
		if (user != null) {
			Writer w = new StringWriter();
			factory.createGenerator(w).writeObject(user);

			return Response.ok(toJSON(user)).build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "insert user based on passed in object", httpMethod = "POST")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Successful insert",
                			responseHeaders = @ResponseHeader(name = "Location", description = "Get URI for newly inserted user", response = URI.class)),
			@ApiResponse(code = 500, message = "Internal error", response = User.class),
			@ApiResponse(code = 406, message = "Invalid data", response = User.class),
			@ApiResponse(code = 409, message = "User exists with the passed in id", response = User.class) })
	public Response insertUser(@ApiParam(name = "user", value = "user object", required = true) User user, @Context UriInfo uriInfo)
			throws JSONException, IOException {

		EntityTransaction utx = null;
		try {
			if (user.getId() <= 0L) {
				return Response.notAcceptable(null).entity(toJSON(user)).build();
			}
			EntityManager em = emf.createEntityManager();
			User user1 = em.find(User.class, user.getId());
			if (user1 != null) {
				return Response.status(Response.Status.CONFLICT).entity(toJSON(user1)).build();
			}
			utx = em.getTransaction();
			if (utx == null) {
				return Response.serverError().entity(toJSON(user)).build();
			}
			utx.begin();
			em.persist(user);
			utx.commit();
		} catch (Exception e) {
			try {
				if (utx != null)
					utx.rollback();
			} catch (Throwable e1) {
				// TODO for debugging only		
				e1.printStackTrace();
			}
			return Response.serverError().entity(toJSON(user)).build();
		}
		UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Long.toString(user.getId()));

		return Response.created(builder.build()).build();
	}

	@Path("{id}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "update user from passed in object", httpMethod = "PUT")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Successful update"),
	        @ApiResponse(code = 500, message = "Internal error", response = User.class),
			@ApiResponse(code = 404, message = "No user exists for id", response = User.class) })
	public Response updateUser(@ApiParam(name = "id", value = "user id", required = true) @PathParam("id") long id,
 	                           @ApiParam(name = "user", value = "user object", required = true) User user)
			throws JSONException, IOException {

		EntityTransaction utx = null;
		try {
			if (user.getId() <= 0L || user.getId() != id) {
				return Response.status(Response.Status.NOT_FOUND).entity(toJSON(user)).build();
			}
			EntityManager em = emf.createEntityManager();
			User user1 = em.find(User.class, id);
			if (user1 == null) {
				return Response.status(Response.Status.NOT_FOUND).entity(toJSON(user)).build();
			}
			utx = em.getTransaction();
			if (utx == null) {
				return Response.serverError().entity(toJSON(user)).build();
			}
			utx.begin();
			user1.setFirstName(user.getFirstName());
			user1.setLastName(user.getLastName());
			utx.commit();
		} catch (Exception e) {
			try {
				if (utx != null)
					utx.rollback();
			} catch (Throwable e1) {
				// TODO for debugging only		
				e1.printStackTrace();
			}						
			return Response.serverError().entity(toJSON(user)).build();
		}
		
		return Response.noContent().build();
	}	

	@Path("{id}")
	@DELETE
	@ApiOperation(value = "update user from passed in object", httpMethod = "DELETE")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Successful delete"),
	        @ApiResponse(code = 500, message = "Internal error"),
			@ApiResponse(code = 404, message = "No user exists for id") })
	public Response deleteUser(@ApiParam(name = "id", value = "user id", required = true) @PathParam("id") long id)
			throws JSONException, IOException {

		EntityTransaction utx = null;
		try {
			EntityManager em = emf.createEntityManager();
			User user1 = em.find(User.class, id);
			if (user1 == null) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			utx = em.getTransaction();
			if (utx == null) {
				return Response.serverError().build();
			}
			utx.begin();
			em.remove(user1);
			utx.commit();
		} catch (Exception e) {
			try {
				if (utx != null)
					utx.rollback();
			} catch (Throwable e1) {
				// TODO for debugging only		
				e1.printStackTrace();
			}						
			return Response.serverError().build();
		}
		
		return Response.noContent().build();
	}
	
	private String toJSON (User user) throws IOException {
		Writer w = new StringWriter();
		factory.createGenerator(w).writeObject(user);
		return w.toString();
	}

}