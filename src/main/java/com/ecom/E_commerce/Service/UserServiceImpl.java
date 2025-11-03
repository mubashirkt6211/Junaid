package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Config.JwtService;
import com.ecom.E_commerce.Exception.EmailAlreadyExistsException;
import com.ecom.E_commerce.Model.Cart;
import com.ecom.E_commerce.Model.Item;
import com.ecom.E_commerce.Model.User;
import com.ecom.E_commerce.Repository.CartRepository;
import com.ecom.E_commerce.Repository.ItemRepository;
import com.ecom.E_commerce.Repository.UserRepository;
import com.ecom.E_commerce.Request.RegisterRequest;
import com.ecom.E_commerce.Response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public Optional<User> findUserByEmail(String mail) {
        return userRepository.findByEmail(mail);
    }

    @Override
    public Optional<User> findUserByJwtToken(String jwt) {
        if (jwt == null || jwt.isBlank()) {
            return Optional.empty();
        }
        try {
            String email = jwtService.extractUsername(jwt);
            return findUserByEmail(email);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public AuthenticationResponse register(RegisterRequest request) throws EmailAlreadyExistsException {
        Optional<User> isExist = userRepository.findByEmail(request.getEmail());
        if (isExist.isPresent()) {
            throw new EmailAlreadyExistsException("Email is already exist!");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        String token = jwtService.generateToken(savedUser);

        return AuthenticationResponse.builder()
                .message("User registered successfully")
                .token(token)
                .build();
    }

    @Override
    public String login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(user);
    }
}
