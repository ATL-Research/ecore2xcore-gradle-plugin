/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package fr.naomod.ecore2xcore;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import static fr.naomod.ecore2xcore.Ecore2XcoreGenerator.build;

/**
 * A simple functional test for the 'fr.naomod.ecore2xcore.greeting' plugin.
 */
class Ecore2xcoreGradlePluginFunctionalTest {
    @TempDir
    File projectDir;

    private File getBuildFile() {
        return new File(projectDir, "build.gradle");
    }

    private File getSettingsFile() {
        return new File(projectDir, "settings.gradle");
    }

    private File getEcoreFile() {
        File modelDir = new File(projectDir, "models");
        modelDir.mkdir();
        return new File(modelDir, "Graf.ecore");
    }

    private File getXcoreFile() {
        return new File(projectDir, "build/xcore-gen/Graf.xcore");
    }

    @Test void canRunTask() throws IOException {
        writeString(getSettingsFile(), "");
        writeString(getBuildFile(),
            "plugins {" +
            "  id('io.github.ATL-Research.ecore2xcore')" +
            "}");
        writeString(getEcoreFile(),
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
            "<ecore:EPackage xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\" name=\"pack1\" nsURI=\"http://ATOL\" nsPrefix=\"pack\">" + 
            "   <eClassifiers xsi:type=\"ecore:EClass\" name=\"Java\"/>" + 
            "</ecore:EPackage>"
        );

        // Run the build
        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments("generateXcore");
        runner.withProjectDir(projectDir);
        BuildResult result = runner.build();

        // Verify the result
        assertTrue(result.getOutput().contains("Processing Graf.ecore"));

        // Target file is correctly created
        File resultXcore = getXcoreFile();
        assertTrue(resultXcore.exists());
        
        // Content is OK
        File expectedOutput = Files.createTempFile("xcore", "").toFile();
        build("atl.research", getEcoreFile(), expectedOutput);
        String expectedContent = Files.readString(Path.of(expectedOutput.getAbsolutePath()));

        String contentXcore = Files.readString(Path.of(resultXcore.getAbsolutePath()));
        
        assertEquals(expectedContent, contentXcore);
        
    }

    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
