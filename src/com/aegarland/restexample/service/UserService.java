package com.aegarland.restexample.service;

import java.io.IOException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.json.JSONException;
import org.springframework.data.domain.PageRequest;

import com.aegarland.restexample.entity.User;

public interface UserService {

	public Response getUsers(PageRequest request) throws JSONException, IOException;

	public Response getUser(long id) throws JSONException, IOException;

	public Response insertUser(User user, UriBuilder builder) throws JSONException, IOException;

	public Response updateUser(long id, User user) throws JSONException, IOException;

	public Response deleteUser(long id) throws JSONException, IOException;
}