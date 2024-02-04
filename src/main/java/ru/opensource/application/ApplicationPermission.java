package ru.opensource.application;

import lombok.Data;

@Data
public class ApplicationPermission {
    private boolean granted;
    private String permissionName;
}
