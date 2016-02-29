package com.aegarland.restexample.dao;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.aegarland.restexample.entity.User;

@Repository
public class UserDaoJpaImpl implements UserDao {

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public List<User> getUsers() throws IOException {
		TypedQuery<User> typedQuery = em.createQuery("SELECT u FROM User u", User.class);
		return typedQuery.getResultList();
	}

	@Override
	public User getUser(long id) throws IOException {
		return em.find(User.class, id);
	}

	@Override
	public boolean insertUser(User user) throws IOException {
		try {
			User user1 = em.find(User.class, user.getId());
			if (user1 != null) {
				return false;
			}
			em.persist(user);
		} catch (Exception e) {			
			return false;
		}
		return true;
	}

	@Override
	public boolean updateUser(long id, User user) throws IOException {
		try {
			if (user.getId() <= 0L || user.getId() != id) {
				return false;
			}
			User user1 = em.find(User.class, id);
			if (user1 == null) {
				return false;
			}
			user1.setFirstName(user.getFirstName());
			user1.setLastName(user.getLastName());
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	@Override
	public boolean deleteUser(long id) throws IOException {
		try {
			User user1 = em.find(User.class, id);
			if (user1 == null) {
				return false;
			}
			em.remove(user1);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

}