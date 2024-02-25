package ru.opensource.adblibrary.application;

import lombok.Data;
import ru.opensource.adblibrary.permission.ApplicationPermission;

import java.util.ArrayList;

@Data
public class AndroidApplication {
    private String path;
    private String packageName;
    private String sha1;
    private String sha256;
    private String sha512;
    private ArrayList<ApplicationPermission> runtimePermissions = new ArrayList<>();
    private ArrayList<ApplicationPermission> installPermissions = new ArrayList<>();
    private ArrayList<ApplicationPermission> requestedPermissions = new ArrayList<>();

    private ArrayList<ApplicationPermission> dangerousPermissions = new ArrayList<>();

    public void addRuntimePermission(ApplicationPermission permission){
        this.runtimePermissions.add(permission);
    }

    public void addInstallPermissions(ApplicationPermission permission){
        this.installPermissions.add(permission);
    }

    public void addRequestedPermissions(ApplicationPermission permission){
        this.requestedPermissions.add(permission);
    }
}
