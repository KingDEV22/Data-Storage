package com.data.organization.service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.data.organization.dto.RegistrationRequest;
import com.data.organization.model.OrgUser;
import com.data.organization.model.Role;
import com.data.organization.repository.OrgUserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final OrgUserRepository oRepository;
    private final PasswordEncoder encoder;
    private final MongoTemplate mongoTemplate;

    private boolean validateEmail(String emailAddress) {
        final String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    private String getCountryCode(String countryName) {
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            if (countryName.equalsIgnoreCase(locale.getDisplayCountry())) {
                return locale.getCountry();
            }
        }
        return "N/A";
    }

    private String generateId(String name, String country) {
        String code = getCountryCode(country);
        char firstLetter = name.charAt(0);
        char lastLetter = name.charAt(name.length() - 1);
        return String.valueOf(firstLetter) + lastLetter + code + (mongoTemplate.count(null, OrgUser.class) + 1);

    }

    public void register(RegistrationRequest request) {
        if (!validateEmail(request.getEmail())) {
            throw new UsernameNotFoundException("User not valid");
        }
        if (oRepository.existsByEmail(request.getEmail())) {
            throw new UsernameNotFoundException("User already exists. Please try with new one");
        }
        Set<Role> roles = new HashSet<>();
        roles.add(request.getAdmin() ? Role.ROLE_ADMIN : Role.ROLE_USER);
        OrgUser user = new OrgUser(
                generateId(request.getName(), request.getCountry()),
                request.getName(),
                encoder.encode(request.getPassword()),
                request.getEmail(),
                request.getAddress(),
                request.getCountry(),
                roles,
                false,
                true);
        oRepository.save(user);
    }

}
