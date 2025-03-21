package mystudy.study.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.security.CustomUserDetail;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @GetMapping("/api/test")
    public String test(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetail userInfo = (CustomUserDetail) principal;

        log.info("사용자 정보: {}, {}", userInfo.getUsername(), userInfo.getNickname());
        

        log.info("TestController test 요청 들어옴");
        log.info("authorization access 토큰 확인: {}", authorization);
        return "test";
    }
}
