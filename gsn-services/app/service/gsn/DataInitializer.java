package service.gsn;

import models.gsn.auth.SecurityRole;

import java.util.Arrays;

/**
 * Data initializer class.
 */
public class DataInitializer {
    public DataInitializer() {
        if (SecurityRole.find.query().findCount() == 0) {
            for (final String roleName : Arrays
                    .asList(controllers.gsn.auth.Application.USER_ROLE)) {
                final SecurityRole role = new SecurityRole();
                role.roleName = roleName;
                role.save();
            }
            for (final String roleName : Arrays
                    .asList(controllers.gsn.auth.Application.ADMIN_ROLE)) {
                final SecurityRole admin = new SecurityRole();
                admin.roleName = roleName;
                admin.save();
            }
        }
    }
}
