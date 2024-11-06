package com.tuntun.user_service.controller;


import com.tuntun.interface_center.user_interface.UserFeign;
import com.tuntun.interface_center.user_interface.dto.*;
import com.tuntun.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {



    private final UserService userService;
    private final UserFeign userFeign;

    @GetMapping
    public Page<UserResponseDTO> getUsers(UserRequestDTO requestDTO, Pageable pageable) throws Exception {
       CreateUserRequestDTO requestDTO1 = new CreateUserRequestDTO();
        userFeign.createUsers(requestDTO1);
        return userService.getUser(requestDTO,pageable);
    }


    @PostMapping
    public CreateUserResponseDTO createUsers(@RequestBody CreateUserRequestDTO requestDTO) throws Exception {
        return userService.createUser(requestDTO);
    }


    @PostMapping("/login")
    public LoginUserResponseDTO loginUser(@RequestBody LoginUserRequestDTO requestDTO) throws Exception {
        return userService.loginUser(requestDTO);
    }




}
