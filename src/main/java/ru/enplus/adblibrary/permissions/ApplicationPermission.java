package ru.enplus.adblibrary.permissions;

import lombok.Data;
import ru.enplus.adblibrary.properties.AndroidAppPermissions;

/**
 * Represents a permission granted or requested by an Android application.
 */
@Data
public class ApplicationPermission {

    /**
     * Indicates whether the permission is granted.
     */
    private boolean granted;

    /**
     * The name of the permission.
     */
    private String permissionName;

    /**
     * Checks if the permission is categorized as dangerous.
     *
     * @return {@code true} if the permission is considered dangerous according to AndroidAppPermissions.DANGEROUS_PERMISSIONS; {@code false} otherwise.
     */
    public boolean isDangerous() {
        return AndroidAppPermissions.DANGEROUS_PERMISSIONS.contains(permissionName);
    }
}
