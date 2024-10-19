package com.project.bookMembership.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo; 

    @Autowired
    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Long getUserIdByUsername(String username) {
        User user = userRepo.findByEmail(username).orElse(null);

   
        return (user != null) ? user.getIdUser() : null;
    }

    

    @Override
    public Optional<User> getUserByTrainerId(Long id) {
        // Find the user by ID, returning an Optional
        return userRepo.findByIdTrainer(id);
    }
    
    

}
