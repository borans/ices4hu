package com.pointers.ices4hu.services;

import com.pointers.ices4hu.repositories.UserRepository;
import com.pointers.ices4hu.security.jwt.JwtUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails loadUserByID(Long id)
    {
        return JwtUserDetails.
                getUserDetailsInstanceFor(userRepository.findById(id).get());
    }

    @Override
    public UserDetails loadUserByUsername(String loginID) throws UsernameNotFoundException {
        return JwtUserDetails.
                getUserDetailsInstanceFor(userRepository.findByLoginID(loginID));
    }
}
