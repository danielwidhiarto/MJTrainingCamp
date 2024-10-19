package com.project.bookMembership.user;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Long getUserIdByUsername(String username);

    Optional<User> getUserByTrainerId(Long id);
}