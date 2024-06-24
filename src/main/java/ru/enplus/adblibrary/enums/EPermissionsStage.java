package ru.enplus.adblibrary.enums;

/**
 * Enum representing different stages of permissions collection for an Android application.
 */
public enum EPermissionsStage {
    /**
     * Collecting requested permissions stage.
     */
    COLLECT_REQUESTED_PERMISSIONS,

    /**
     * Collecting install permissions stage.
     */
    COLLECT_INSTALL_PERMISSIONS,

    /**
     * Collecting runtime permissions stage.
     */
    COLLECT_RUNTIME_PERMISSIONS,

    /**
     * Awaiting stage.
     */
    AWAITING
}
