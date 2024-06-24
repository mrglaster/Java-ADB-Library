package ru.enplus.adblibrary.application;

import lombok.Data;
import ru.enplus.adblibrary.permissions.ApplicationPermission;

import java.util.ArrayList;

/**
 * Represents an Android application with details such as its path, package name, cryptographic hashes,
 * and various permissions associated with it.
 */
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

    /**
     * Adds a runtime permission to the application.
     *
     * @param permission the runtime permission to add
     */
    public void addRuntimePermission(ApplicationPermission permission){
        this.runtimePermissions.add(permission);
    }

    /**
     * Adds an install permission to the application.
     *
     * @param permission the install permission to add
     */
    public void addInstallPermission(ApplicationPermission permission){
        this.installPermissions.add(permission);
    }

    /**
     * Adds a requested permission to the application.
     *
     * @param permission the requested permission to add
     */
    public void addRequestedPermission(ApplicationPermission permission){
        this.requestedPermissions.add(permission);
    }

    /**
     * Adds a dangerous permission to the application.
     *
     * @param permission the dangerous permission to add
     */
    public void addDangerousPermission(ApplicationPermission permission){
        this.dangerousPermissions.add(permission);
    }
}
