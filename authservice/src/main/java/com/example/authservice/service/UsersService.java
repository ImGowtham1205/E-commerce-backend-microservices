package com.example.authservice.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.authservice.model.AdminCache;
import com.example.authservice.model.Admins;
import com.example.authservice.model.UserCache;
import com.example.authservice.model.UserPrincipal;
import com.example.authservice.model.Users;
import com.example.authservice.repository.AdminRepo;
import com.example.authservice.repository.UserRepo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class UsersService implements UserDetailsService {

    private final UserRepo userRepo;
    private final AdminRepo adminRepo;
    private final UsersServiceCache usersServiceCache;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserCache user = getUser(email);
        if (user != null)
            return new UserPrincipal(user);

        AdminCache admin = getAdmin(email);
        if (admin != null)
            return new UserPrincipal(admin);

        throw new UsernameNotFoundException("User Not Found");
    }

    public Users saveUser(Users user) {
        return userRepo.save(user);
    }

    public Admins saveAdmin(Admins admin) {
        return adminRepo.save(admin);
    }
    
    public Users getUserEntity(String email) {
        return userRepo.findByEmail(email);
    }
    
    public Admins getAdminEntity(String email) {
        return adminRepo.findByEmail(email);
    }
    
    public UserCache getUserById(long id) {
        return usersServiceCache.getUserById(id);
    }

    public UserCache getUserByPhoneNo(String phoneno) {
       return usersServiceCache.getUserByPhoneNo(phoneno);
    }
    
    public UserCache getUser(String email) {
       return usersServiceCache.getUser(email);
    }
     
    public AdminCache getAdmin(String email) {
        return usersServiceCache.getAdmin(email);
    }

    public UserCache updateUserProfile(Users user) {
        return usersServiceCache.updateUserProfile(user);
    }
     
    public Users deleteByUserEmail(String email) {
        return usersServiceCache.deleteByUserEmail(email);
    }
    
    public Admins deleteByAdminEmail(String email) {
        return usersServiceCache.deleteByAdminEmail(email);
    }
     
}