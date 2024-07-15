package org.mounanga.userservice.web;

import org.mounanga.userservice.dto.PwdUpdateRequest;
import org.mounanga.userservice.dto.UserPwdRequest;
import org.mounanga.userservice.service.SecurityService;
import org.mounanga.userservice.util.SecurityInformation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/securities")
public class SecurityRestController {

    private final SecurityService securityService;
    private final SecurityInformation securityInformation;

    public SecurityRestController(SecurityService securityService, SecurityInformation securityInformation) {
        this.securityService = securityService;
        this.securityInformation = securityInformation;
    }

    @PutMapping("/update")
    public void updatePassword(UserPwdRequest request){
        securityService.updatePassword(request);
    }

    @GetMapping("/request/{email}")
    public void requestToChangePassword(@PathVariable String email){
        securityService.requestToChangePassword(email);
    }

    @PreAuthorize("hasAnyAuthority('USER','MODERATOR','ADMIN','SUPER_ADMIN')")
    @PutMapping("/changePwd")
    public void changePassword(@RequestBody PwdUpdateRequest request){
        String username = securityInformation.getCurrentUsername();
        securityService.changePassword(username, request.password(), request.confirmPassword());
    }

}
