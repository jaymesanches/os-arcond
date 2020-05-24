package br.com.js.base.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.js.base.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	public Optional<User> findByEmail(String email);
	public List<User> findByNameIgnoreCaseContaining(String name);
	public List<User> findByRolesName(String roleName);
	public boolean existsByNameIgnoreCaseContaining(String name);
}
