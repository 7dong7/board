package mystudy.study.security.config;

public class AccessURL {

    public static final String[] RESOURCE = {
            "/css/**", "/js/**", "/favicon.ico",
            "/images/**", "/upload/**"
    };

    public static final String[] WHITELIST = { // 권한이 없어도 접근 가능한
            "/",        // 메인 페이지
            "/login", "/logout",        // 로그인 & 로그아웃
            "/members/*",               // 회원 정보 보기
            "/posts", "/posts/*",       // 게시글
            "/oauth2/**",               // oauth2 로그인



    };

    public static final String[] BLACKLIST = { // 접근 불가
    };

    // 권한이 USER 인 회원만 가능
    public static final String[] USER_ROUTE = { // 유저 접근가능
            // 로그인한 사용자
            "/comments/*/new", "/comments/*/new/*",

            // 권한 변경 예정 role : ROLE_USER
            "/members/*/edit", "/members/*/delete", "/members/*/passwordEdit",

            "/image/upload",
            "/posts/new/post",
            "/posts/*/edit",

            // 테스트
            "/api/test"
    };

    // 권한이 ADMIN 인 회원만 가능
    public static final String[] ADMIN_ROUTE = { //
            "/admin", "/admin/**",

            // 회원 변경 예정 role : ROLE_ADMIN
            "/members" // 회원 검색 기능
    };

}
