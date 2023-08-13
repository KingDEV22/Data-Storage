package com.data.organization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.data.organization.model.AppUserDetails;
import com.data.organization.model.OrgUser;

@Service
public class OrgUserServiceImpl implements UserDetailsService {

    @Autowired
    private FormUtilService fService;

    @Override
    @Transactional
    public AppUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        OrgUser user = fService.getOrgUser(email);
        return AppUserDetails.build(user);
    }

}
