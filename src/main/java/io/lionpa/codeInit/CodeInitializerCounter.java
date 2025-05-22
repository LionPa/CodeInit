package io.lionpa.codeInit;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class CodeInitializerCounter {

    public static int countJarsWithCodeInitializer(File directory) {
        int count = 0;
        File[] files = directory.listFiles();

        if (files == null) {
            return 0;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".jar")) {
                if (containsCodeInitializer(file)) {
                    count++;
                }
            }
        }

        return count;
    }

    private static boolean containsCodeInitializer(File jarFile) {
        try (ZipFile zip = new ZipFile(jarFile)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.equalsIgnoreCase(".codeinitializer") || name.equalsIgnoreCase(".codeinit")) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении JAR файла: " + jarFile.getPath());
            e.printStackTrace();
        }

        return false;
    }
}