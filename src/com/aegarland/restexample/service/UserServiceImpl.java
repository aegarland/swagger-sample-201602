package com.aegarland.restexample.service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegarland.restexample.dao.UserDao;
import com.aegarland.restexample.entity.User;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

@Service
public class UserServiceImpl implements UserService {

	private JsonFactory factory = new JsonFactory();

	{
		factory.enable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
		factory.enable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
		factory.setCodec(new com.fasterxml.jackson.databind.ObjectMapper(factory));
	}

	@Autowired
	private UserDao userDao;

	@Override
	public Response getUsers() throws JSONException, IOException {
		Writer w = new StringWriter();
		User[] users = new User[0];
		try {
			users = userDao.getUsers().toArray(users);
		} catch (Throwable t) {
			// TODO log this or take some other more sensible action
			return Response.serverError().entity(t.getMessage()).build();
		}
		factory.createGenerator(w).writeObject(users);

		return Response.ok(w.toString()).build();
	}

	@Override
	public Response getUser(long id) throws JSONException, IOException {

		User user = null;

		try {
			user = userDao.getUser(id);
		} catch (Throwable e) {
			// TODO log this or take some other more sensible action
		}
		if (user != null) {
			Writer w = new StringWriter();
			factory.createGenerator(w).writeObject(user);

			return Response.ok(toJSON(user)).build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@Override
	public Response insertUser(User user, UriBuilder builder) throws JSONException, IOException {
		try {
			if (!userDao.insertUser(user)) {
				return Response.status(Response.Status.CONFLICT).entity(toJSON(user)).build();
			}
		} catch (Throwable t) {
			return Response.serverError().entity(toJSON(user)).build();
		}
		builder.path(Long.toString(user.getId()));

		return Response.created(builder.build()).build();
	}

	@Override
	public Response updateUser(long id, User user) throws JSONException, IOException {
		try {
			if (!userDao.updateUser(id, user)) {
				return Response.status(Response.Status.NOT_FOUND).entity(toJSON(user)).build();
			}
		} catch (Throwable t) {
			return Response.serverError().entity(toJSON(user)).build();
		}

		return Response.noContent().build();
	}

	@Override
	public Response deleteUser(long id) throws JSONException, IOException {
		try {
			if (!userDao.deleteUser(id)) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (Throwable t) {
			return Response.serverError().build();
		}

		return Response.noContent().build();
	}

	private String toJSON(User user) throws IOException {
		Writer w = new StringWriter();
		factory.createGenerator(w).writeObject(user);
		return w.toString();
	}
}