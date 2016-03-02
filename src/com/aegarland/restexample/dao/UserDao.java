package com.aegarland.restexample.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.aegarland.restexample.entity.User;

public interface UserDao extends Repository<User,Long> {

	public Page<User> findAll(Pageable pageable);

	public User findOne(Long id);

	public <S extends User> S save(S user);

	public void delete(Long id);
}