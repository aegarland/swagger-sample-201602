package com.aegarland.restexample.dao;

import java.io.IOException;
import java.util.List;

import com.aegarland.restexample.entity.User;

public interface UserDao {

	public List<User> getUsers() throws IOException;

	public User getUser(long id) throws IOException;

	public boolean insertUser(User user) throws IOException;

	public boolean updateUser(long id, User user) throws IOException;

	public boolean deleteUser(long id) throws IOException;
}