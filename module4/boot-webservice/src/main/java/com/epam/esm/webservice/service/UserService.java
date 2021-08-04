package com.epam.esm.webservice.service;

import com.epam.esm.webservice.dto.UserDTO;

public interface UserService extends PageableResourceService<UserDTO> {

    void register(String email, String password);

    UserDTO findByEmail(String email);

    UserDTO findByEmailAndPassword(String email, String password);
}
