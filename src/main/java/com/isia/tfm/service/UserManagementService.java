package com.isia.tfm.service;

import com.isia.tfm.model.CreateUser201Response;
import com.isia.tfm.model.User;

public interface UserManagementService {
    /**
     *
     * @param user the user
     * @return {@link CreateUser201Response}
     */
    CreateUser201Response createUser(User user);
}
