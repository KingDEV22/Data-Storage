package com.data.organization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.data.organization.model.AppUserDetails;
import com.data.organization.model.OrgUser;
import com.data.organization.repository.OrgUserRepo;

@Service
public class OrgUserServiceImpl implements UserDetailsService {

    @Autowired
    private OrgUserRepo orgUserRepository;

    @Override
    @Transactional
    public AppUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        OrgUser user = orgUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return AppUserDetails.build(user);
    }

}
