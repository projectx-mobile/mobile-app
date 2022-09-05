package com.jungeeks.email.repo;

import com.jungeeks.email.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);

}
