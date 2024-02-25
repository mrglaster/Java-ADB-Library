package ru.opensource.adblibrary.permission;

import lombok.Data;

@Data
public class ApplicationPermission {
    private boolean granted;
    private String permissionName;
}
