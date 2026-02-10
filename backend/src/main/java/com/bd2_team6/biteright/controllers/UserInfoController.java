package com.bd2_team6.biteright.controllers;

import com.bd2_team6.biteright.controllers.DTO.UserInfoDTO;
import com.bd2_team6.biteright.controllers.requests.update_requests.UpdateWeightRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.UserInfoUpdateRequest;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.entities.user_info.UserInfo;
import com.bd2_team6.biteright.service.UserInfoService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/userInfo")
@RequiredArgsConstructor
public class UserInfoController {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);
    private final UserInfoService userInfoService;
    private final UserRepository userRepository;

    @GetMapping("/findUserInfo")
    public ResponseEntity<?> findUserInfo(Authentication authentication) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            UserInfo userInfo = userInfoService.findUserInfoByUsername(username);
            UserInfoDTO userInfoDTO = new UserInfoDTO(userInfo.getUserInfoId(), userInfo.getName(), userInfo.getSurname(),
                    userInfo.getAge(), userInfo.getWeight(), userInfo.getHeight(), userInfo.getLifestyle(), userInfo.getBmi());
            return ResponseEntity.ok(userInfoDTO);
        }
        catch (IllegalArgumentException e) { // TODO: DO WE NEED THIS EXCEPTION?
            logger.error("Error finding user info." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error finding user info." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/weight")
    public ResponseEntity<?> getWeight(Authentication authentication) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            UserInfo userInfo = userInfoService.findUserInfoByUsername(username);
            return ResponseEntity.ok(userInfo.getWeight());
        } catch (Exception e) {
            logger.error("Error finding weight." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserInfo(Authentication authentication, @RequestBody UserInfoUpdateRequest request) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            UserInfo userInfo = userInfoService.updateUserInfo(username, request);
            UserInfoDTO userInfoDTO = new UserInfoDTO(userInfo.getUserInfoId(), userInfo.getName(), userInfo.getSurname(),
                    userInfo.getAge(), userInfo.getWeight(), userInfo.getHeight(), userInfo.getLifestyle(), userInfo.getBmi());
            return ResponseEntity.ok(userInfoDTO);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error updating user info." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating user info." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/updateWeight")
    public ResponseEntity<?> updateWeight(Authentication authentication, @RequestBody UpdateWeightRequest request) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            userInfoService.updateUsersWeight(username, request.getWeight());
            return ResponseEntity.ok("Weight updated successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Error updating weight." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating weight." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
