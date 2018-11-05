package com.milan.tipster.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.milan.tipster.dao.ManagerRepository;
import com.milan.tipster.model.Manager;

@Component
public class DatabaseLoader implements CommandLineRunner {

	private final ManagerRepository managers;

	@Autowired
	public DatabaseLoader(ManagerRepository managerRepository) {
		this.managers = managerRepository;
	}

	@Override
	public void run(String... strings) throws Exception {

		Manager milan = this.managers.save(new Manager("Milan", "123",
							"ROLE_MANAGER"));
		Manager anastasia = this.managers.save(new Manager("Anastasia", "123",
							"ROLE_MANAGER"));

		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken("Milan", "doesn't matter",
				AuthorityUtils.createAuthorityList("ROLE_MANAGER")));

		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken("Anastasia", "doesn't matter",
				AuthorityUtils.createAuthorityList("ROLE_MANAGER")));


		SecurityContextHolder.clearContext();
	}
}