package juno.detector;

import com.fazecast.jSerialComm.SerialPort;
import juno.logger.JunoLogger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class JunoDetector {

    private static final Path JUNO_IDF_ROOT = getDefaultIdfPath();

    private static Path getDefaultIdfPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return Paths.get("C:", "Juno", "esp-idf-v5.4.2");
        } else {
            return Paths.get(System.getProperty("user.home"), "Juno", "esp-idf-v5.4.2");
        }
    }

    public static String detectIdfPath() {
        return Files.exists(JUNO_IDF_ROOT) ? JUNO_IDF_ROOT.toString() : null;
    }

    public static String detectTool(String executableName) {
        File toolPath = searchFileRecursively(JUNO_IDF_ROOT.toFile(), executableName);
        return (toolPath != null) ? toolPath.getParent() : null;
    }

    public static String detectPythonPath() {
        return detectTool(isWindows() ? "python.exe" : "python");
    }

    public static String detectPythonExecutable() {
        File file = searchFileRecursively(JUNO_IDF_ROOT.toFile(), isWindows() ? "python.exe" : "python");
        return (file != null) ? file.getAbsolutePath() : null;
    }

    public static String detectToolchainBin() {
        return detectTool(isWindows() ? "xtensa-esp-elf-gcc.exe" : "xtensa-esp-elf-gcc");
    }

    public static String detectCcacheBin() {
        return detectTool(isWindows() ? "ccache.exe" : "ccache");
    }

    public static String detectEspressifGitPath() {
        if (isWindows()) {
            return String.valueOf(JUNO_IDF_ROOT.resolve("cmd").resolve("git.exe"));
        } else {
            return String.valueOf(JUNO_IDF_ROOT.resolve("bin").resolve("git"));
        }
    }

    public static String detectCmakePath() {
        return detectTool(isWindows() ? "cmake.exe" : "cmake");
    }

    public static String detectNinjaPath() {
        return detectTool(isWindows() ? "ninja.exe" : "ninja");
    }

    public static String detectIdfPyPath() {
        File idfPy = new File(JUNO_IDF_ROOT.toFile(), "tools/idf.py");
        return idfPy.exists() ? idfPy.getParent() : null;
    }

    public static String detectXtensaGdbPath() {
        return detectTool(isWindows() ? "xtensa-esp32-elf-gdb.exe" : "xtensa-esp32-elf-gdb");
    }

    public static String detectXtensaToolchainPath() {
        return detectTool(isWindows() ? "xtensa-esp-elf-gcc.exe" : "xtensa-esp-elf-gcc");
    }

    public static String detectDfuUtilBin() {
        return detectTool(isWindows() ? "dfu-util.exe" : "dfu-util");
    }

    public static String detectOpenOcdBin() {
        return detectTool(isWindows() ? "openocd.exe" : "openocd");
    }

    public static String detectOpenOcdScriptsPath() {
        File file = searchFileRecursively(JUNO_IDF_ROOT.toFile(), "memory.tcl");
        return (file != null) ? file.getParent() : null;
    }

    public static String detectEspClangPath() {
        return detectTool(isWindows() ? "clang.exe" : "clang");
    }

    public static String detectEsp32Port() {
        for (SerialPort port : SerialPort.getCommPorts()) {
            String desc = port.getDescriptivePortName().toLowerCase();
            String name = port.getSystemPortName().toLowerCase();
            if (desc.contains("ch340") || desc.contains("usb serial") || desc.contains("cp210x") || desc.contains("ftdi") || name.contains("usbserial") || name.contains("ttyusb") || name.contains("cu.usbserial")) {
                return port.getSystemPortName();
            }
        }
        return null;
    }

    public static void printDetectedPaths() {
        JunoLogger.info("--- JUNO Detection Report ---");
        JunoLogger.info("IDF Path → " + detectIdfPath());
        JunoLogger.info("idf.py Path → " + detectIdfPyPath());
        JunoLogger.info("Python Path → " + detectPythonPath());
        JunoLogger.info("Python Executable → " + detectPythonExecutable());
        JunoLogger.info("Toolchain Bin → " + detectToolchainBin());
        JunoLogger.info("Ccache Bin → " + detectCcacheBin());
        JunoLogger.info("Git Path → " + detectEspressifGitPath());
        JunoLogger.info("CMake Path → " + detectCmakePath());
        JunoLogger.info("Ninja Path → " + detectNinjaPath());
        JunoLogger.info("Xtensa GDB → " + detectXtensaGdbPath());
        JunoLogger.info("Xtensa Toolchain Path → " + detectXtensaToolchainPath());
        JunoLogger.info("DFU Util Bin → " + detectDfuUtilBin());
        JunoLogger.info("OpenOCD Bin → " + detectOpenOcdBin());
        JunoLogger.info("OpenOCD Scripts → " + detectOpenOcdScriptsPath());
        JunoLogger.info("ESP32 Serial Port → " + detectEsp32Port());
        JunoLogger.info("------------------------------");
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private static File searchFileRecursively(File dir, String targetFileName) {
        if (dir == null || !dir.exists()) return null;
        File[] files = dir.listFiles();
        if (files == null) return null;

        for (File f : files) {
            if (f.isDirectory()) {
                File result = searchFileRecursively(f, targetFileName);
                if (result != null) return result;
            } else if (f.getName().equalsIgnoreCase(targetFileName) && f.canExecute()) {
                return f;
            }
        }
        return null;
    }
}
