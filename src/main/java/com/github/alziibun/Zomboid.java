package com.github.alziibun;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

// TODO: add console log reading

public class Zomboid {
    private static File jar;
    private static Process process;
    public static File consoleLog;

    private Zomboid () {

    }

    public static void start() throws IOException {
        //start the project zomboid game server process

        // get java arguments
        String os = System.getProperty("os.name");
        String wd = System.getProperty("user.dir");
        String jHome = wd + File.separator + "jre";
        String jBin;
        File startupJSON;
        File home64 = new File(jHome + "64");
        System.out.print("Project Zomboid Server location: ")
        Scanner zScan = new Scanner(System.in);
        String zLoc = zScan.nextLine();


        // if jre64 exists, that means the system supports 64-bit java
        System.out.println("Starting Project Zomboid server");
        System.out.println("Operating System: " + os);
        System.out.println("Java: ");

        // detect 32-bit or 64-bit
        // then assign appropriate startup config
        if (home64.exists()) {
            jHome = home64.getPath();
            startupJSON = new File("ProjectZomboid64.json");
        } else {
            startupJSON = new File("ProjectZomboid32.json");
        }
        jHome = jHome + File.separator + "bin";

        if (os.toLowerCase().contains("windows")) {
            // windows
            jBin = jHome + File.separator + "java.exe";
        } else {
            // linux startup
            jBin = jHome + File.separator + "java";
        }
        System.out.print(jBin);

        // FINISH FINDING JAVA
        // START FINDING ARGUMENTS


        InputStream is = Files.newInputStream(Paths.get(startupJSON.getPath()));
        String JSONText = IOUtils.toString(is, StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(JSONText);
        String mainClass = json.getString("mainClass");
        System.out.println("Main Class: " + mainClass);
        String classpath = json.getJSONArray("classpath").join(";");
        System.out.println("ClassPath: " + classpath);
        List<String> jvmArgs = new ArrayList<>();
        JSONArray jvmArgsJSON = json.getJSONArray("vmArgs");
        for (int i = 0; i < jvmArgsJSON.length(); i++) {
            jvmArgs.add(jvmArgsJSON.getString(i));
        }

        // COMPILE COMMAND

        List<String> command = new ArrayList<>();
        command.add(jBin);
        command.addAll(jvmArgs);
        command.add("-cp");
        command.add(classpath);
        command.add(mainClass);

        // BUILD PROCESS

        ProcessBuilder builder = new ProcessBuilder(command);
        Zomboid.process = builder.inheritIO().start();
    }

    public File getConsoleLog() {
        return new File("console.txt"); //TODO: search for console log
    }
}
