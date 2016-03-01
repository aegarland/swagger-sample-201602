package com.aegarland.restexample.dao;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.aegarland.restexample.entity.User;

public interface UserDao extends Repository<User,Long> {

	public List<User> findAll();

	public User findOne(Long id);

	public <S extends User> S save(S user);

	public void delete(Long id);
}