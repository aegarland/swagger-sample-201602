package com.aegarland.restexample.api;

import java.io.IOException;
import java.net.URI;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.aegarland.restexample.entity.User;
import com.aegarland.restexample.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@Path("/user")
@Api(value = "/user", description = "CRUD operations for users")
@Controller
public class UserResource {

	@Autowired
	private UserService service;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "return a list of all users", notes = "too many results")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful conversion", response = User[].class) })
	public Response getUsers() throws JSONException, IOException {
		return service.getUsers();
	}

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "return user object based on passed in user id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful insert", response = User.class),
			@ApiResponse(code = 404, message = "User not found") })
	public Response getUser(@ApiParam(name = "id", value = "user id", required = true) @PathParam("id") long id)
			throws JSONException, IOException {
		return service.getUser(id);
	}

	@POST
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "insert user based on passed in object", httpMethod = "POST")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Successful insert", responseHeaders = @ResponseHeader(name = "Location", description = "Get URI for newly inserted user", response = URI.class) ),
			@ApiResponse(code = 500, message = "Internal error", response = User.class),
			@ApiResponse(code = 406, message = "Invalid data", response = User.class),
			@ApiResponse(code = 409, message = "User exists with the passed in id", response = User.class) })
	public Response insertUser(@ApiParam(name = "user", value = "user object", required = true) User user,
			                   @Context UriInfo ui) throws JSONException, IOException {
		//could use injected UriInfo instead
		return service.insertUser(user, UriBuilder.fromResource(getClass()));
	}

	@Path("{id}")
	@PUT
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "update user from passed in object", httpMethod = "PUT")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Successful update"),
			@ApiResponse(code = 500, message = "Internal error", response = User.class),
			@ApiResponse(code = 404, message = "No user exists for id", response = User.class) })
	public Response updateUser(@ApiParam(name = "id", value = "user id", required = true) @PathParam("id") long id,
			@ApiParam(name = "user", value = "user object", required = true) User user)
					throws JSONException, IOException {
		return service.updateUser(id, user);
	}

	@Path("{id}")
	@DELETE
	@Transactional
	@ApiOperation(value = "update user from passed in object", httpMethod = "DELETE")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Successful delete"),
			@ApiResponse(code = 500, message = "Internal error"),
			@ApiResponse(code = 404, message = "No user exists for id") })
	public Response deleteUser(@ApiParam(name = "id", value = "user id", required = true) @PathParam("id") long id)
			throws JSONException, IOException {
		return service.deleteUser(id);
	}
}