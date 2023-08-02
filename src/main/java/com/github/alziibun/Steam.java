package com.github.alziibun;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import net.lingala.zip4j.ZipFile;

public class Steam {
    public File cmd;
    public static String os = System.getProperty("os.name");

    public static void install() throws IOException {
        if (os.toLowerCase().contains("windows")) {
            // windows installation
            installForWindows();
        } else {
            installForLinux();

        }
        System.out.println("Steamcmd has been installed.");
    }

    private static void installForWindows() throws IOException {
        URL website = new URL("https://steamcdn-a.akamaihd.net/client/installer/steamcmd.zip");
        File sourcefile = new File("steamcmd.zip");
        FileUtils.copyURLToFile(website, sourcefile);
        try {
            ZipFile steamcmd_zip = new ZipFile(sourcefile);
            steamcmd_zip.extractAll("./steamcmd");
            sourcefile.delete();
        } catch (ZipException | SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private static void installForLinux() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("dpkg --add-architecture i386"); // install steamcmd
        runtime.exec("apt-get update");
        runtime.exec("apt-get install steamcmd"); // this should install steamcmd
    }

    public void run(String path){

    }
}
