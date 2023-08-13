package com.data.organization.service;

import java.util.regex.Pattern;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.data.organization.dto.RegistrationRequest;
import com.data.organization.model.OrgUser;
import com.data.organization.repository.OrgUserRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final OrgUserRepo oRepository;
    private final PasswordEncoder encoder;

    private boolean validateEmail(String emailAddress) {
        final String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    public void register(RegistrationRequest request) {
        if (!validateEmail(request.getEmail())) {
            throw new UsernameNotFoundException("User not valid");
        }
        if (oRepository.existsByEmail(request.getEmail())) {
            throw new UsernameNotFoundException("User already exists. Please try with new one");
        }
        OrgUser user = new OrgUser(
                request.getName(),
                encoder.encode(request.getPassword()),
                request.getEmail(),
                request.getAddress(),
                request.getCountry(),
                request.getRoles(),
                false,
                true);
        System.out.println(user.toString());
        oRepository.save(user);
    }

}
