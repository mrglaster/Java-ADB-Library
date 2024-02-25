package ru.opensource.adblibrary.permission;

import lombok.Data;
import ru.opensource.adblibrary.properties.AndroidAppPermissions;

@Data
public class ApplicationPermission {
    private boolean granted;
    private String permissionName;
    public boolean isDangerous(){
        return AndroidAppPermissions.DANGEROUS_PERMISSIONS.contains(permissionName);
    }
}
