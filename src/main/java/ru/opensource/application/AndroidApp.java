package ru.opensource.application;

import lombok.Data;
import java.util.ArrayList;

@Data
public class AndroidApp {
    private String path;
    private String packageName;
    private String sha1;
    private String sha256;
    private String sha512;
    private ArrayList<String> runtimePermissions = new ArrayList<>();
    private ArrayList<String> installPermissions = new ArrayList<>();
}
