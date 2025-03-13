package net.ink.admin.config.logging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ink.admin.entity.AdminMember;

@AllArgsConstructor
@Getter
public class AdminLoggingEvent {
    private AdminMember actionedAdminMember;
    private String action;
    private String actionQuery;
}
