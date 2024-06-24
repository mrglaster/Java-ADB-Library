package ru.enplus;
import ru.enplus.adblibrary.enums.EPermissionsStage;
import ru.enplus.adblibrary.exceptions.ADBException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws ADBException, IOException, NoSuchAlgorithmException {
        System.out.println(EPermissionsStage.COLLECT_RUNTIME_PERMISSIONS.ordinal());
        System.out.println(EPermissionsStage.COLLECT_REQUESTED_PERMISSIONS.ordinal());
        System.out.println(EPermissionsStage.COLLECT_INSTALL_PERMISSIONS.ordinal());
    }
}