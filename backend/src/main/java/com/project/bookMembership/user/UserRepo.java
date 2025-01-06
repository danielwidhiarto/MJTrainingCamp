package com.project.bookMembership.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    @Query(value = "SELECT * FROM user_table WHERE id_trainer = ?1", nativeQuery = true)
    Optional<User> findByIdTrainer(Long idTrainer);

    @Query(value = "SELECT * FROM user_table WHERE role = 'ROLE_USER'", nativeQuery = true)
    List<User> FindAllUserMember();



}   

