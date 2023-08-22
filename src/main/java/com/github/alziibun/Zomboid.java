package com.github.alziibun;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

// TODO: add console log reading

public class Zomboid {
    private static File jar;
    public static Process server;
    public static File consoleLog;

    private Zomboid () {

    }

    public static void start() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("windows")) {
            try {windows();}
            catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            // TODO: determine possible os strings
            try {
                linux();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void windows() throws IOException {
        //start the project zomboid game server process

        // get java arguments
        String os = System.getProperty("os.name");
        String wd = System.getProperty("user.dir");
        String jHome = wd + File.separator + "jre";
        String jBin;
        File startupJSON;
        File home64 = new File(jHome + "64");


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
        String classpath = json
            .getJSONArray("classpath")
            .toList()
            .stream()
            .map(object -> Objects.toString(object, null))
            .collect(Collectors.joining(";"));
        System.out.println("ClassPath: " + classpath);
        List<String> jvmArgs = new ArrayList<>();
        JSONArray jvmArgsJSON = json.getJSONArray("vmArgs");
        for (int i = jvmArgsJSON.length()-1; i > 0; i--) {
            jvmArgs.add(jvmArgsJSON.getString(i));
        }
        if (os.toLowerCase().contains("windows")) {
            System.out.println("Using windows configuration");
            JSONObject os_conditionals = json.getJSONObject("windows");
            List<Object> os_vmArgs = new ArrayList<>();
            if (os_conditionals != null) {
                if (os.equalsIgnoreCase("windows 7")) {
                    // turn windows 7 args into a JSON object
                    os_vmArgs = os_conditionals
                            .getJSONObject("7")
                            .getJSONArray("vmArgs")
                            .toList();

                } else if (os.equalsIgnoreCase("windows 10")) {
                    // turn windows 10 args into a JSON object
                    os_vmArgs = os_conditionals
                            .getJSONObject("10")
                            .getJSONArray("vmArgs")
                            .toList();
                }
                for (Object obj : os_vmArgs) {
                    jvmArgs.add(obj != null ? obj.toString() : null);
                }
            }
        }
        System.out.println(jvmArgs);

        // COMPILE COMMAND

        List<String> command = new ArrayList<>();
        command.add(jBin);
        command.addAll(jvmArgs);
        command.add("-cp");
        command.add(classpath);
        command.add("zombie/network/GameServer");
        command.add("-statistic 0");


        // BUILD PROCESS
        ProcessBuilder builder = new ProcessBuilder(command);
        builder
                .redirectInput(ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT);
        server = builder.start();
        //input(); //TODO: allow user to write directly
    }

    public static void stop() throws IOException {
        assert server != null;
        assert server.isAlive();
        send("quit");
    }
    public static void linux() throws IOException {
        System.out.println("Starting Linux version of Zomboid Server.");
        List<String> command = new ArrayList<>();
        command.add("sh");
        command.add("start-server.sh");
        ProcessBuilder builder = new ProcessBuilder(command);
        builder
                .redirectInput(ProcessBuilder.Redirect.PIPE)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT);
        server = builder.start();
    }

    public static void enableLogging() {
        assert server != null;
        while (server.isAlive()) {
            server.getInputStream();
        }
    }

    public static void send(String command) {
        assert server != null;
        assert server.isAlive();
        OutputStream stdin = server.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
        try {
            writer.write(command);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File getConsoleLog() {
        return new File("console.txt"); //TODO: search for console log
    }
}
