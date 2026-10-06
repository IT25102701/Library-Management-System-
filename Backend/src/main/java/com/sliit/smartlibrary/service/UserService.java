package com.sliit.smartlibrary.service;
import com.sliit.smartlibrary.dto.RegisterRequest;
import com.sliit.smartlibrary.dto.AdminUserRequest;
import com.sliit.smartlibrary.entity.User;
import java.util.*;
public interface UserService { User register(RegisterRequest r); User createByAdmin(AdminUserRequest r); User getById(Long id); User getByEmail(String email); List<User> getAll(); User update(Long id,User incoming); User updateProfile(Long id,String name,String phone); void changePassword(Long id,String currentPassword,String newPassword); void delete(Long id); }
