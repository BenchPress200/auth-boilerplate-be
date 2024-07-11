package com.auth.boilerplate.user.repository;

import com.auth.boilerplate.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
