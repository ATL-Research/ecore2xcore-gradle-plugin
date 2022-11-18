package fr.naomod.ecore2xcore;

import java.io.File;
import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

public abstract class GenerateXcoreTask extends DefaultTask {
    @InputDirectory
    public abstract DirectoryProperty getSourceDir();

    @Input
    public abstract Property<String> getBasePackage();

    @OutputDirectory
    public abstract DirectoryProperty getTargetDir();

    @TaskAction
    public void generateXcore() throws IOException {
        // only process .ecore files
        // TODO: have a configurable list of extension to process ?
        // TODO: log ignored files ?
        for (File in : getSourceDir().getAsFileTree().filter(f -> f.getName().endsWith(".ecore"))) {
            System.out.println("Processing " + in.getName());
            String targetName = in.getName().replace(".ecore", ".xcore");
            Ecore2XcoreGenerator.build(getBasePackage().get(), in, getTargetDir().file(targetName).get().getAsFile());
        }
    }
}
