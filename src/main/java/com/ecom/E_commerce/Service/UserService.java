package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Exception.EmailAlreadyExistsException;
import com.ecom.E_commerce.Model.Item;
import com.ecom.E_commerce.Model.User;
import com.ecom.E_commerce.Request.RegisterRequest;
import com.ecom.E_commerce.Response.AuthenticationResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByJwtToken(String jwt);


    AuthenticationResponse register(RegisterRequest request) throws EmailAlreadyExistsException;

    String login(String email, String rawPassword);
}
