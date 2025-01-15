package qastudio.backend.domain.user.entity.enums;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role implements GrantedAuthority {
    USER;

    @Override
    public String getAuthority() {
        return name();
    }

    public static boolean isValidAuthority(SimpleGrantedAuthority authority) {
        for (Role role : Role.values()) {
            if (role.getAuthority().equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
