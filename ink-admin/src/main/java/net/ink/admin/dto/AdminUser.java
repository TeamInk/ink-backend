package net.ink.admin.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter;
import net.ink.admin.entity.AdminMember;

@Getter
@Setter
public class AdminUser extends User {
    private AdminMember adminMember;

    public AdminMember.RANK getAdminRank() {
        return adminMember.getAdminRank();
    }

    public AdminUser(AdminMember adminMember, Collection<? extends GrantedAuthority> authorities) {
        super(adminMember.getEmail(), adminMember.getPassword(), authorities);
        this.adminMember = adminMember;
    }

    public AdminUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public AdminUser(String username, String password, boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }
}
