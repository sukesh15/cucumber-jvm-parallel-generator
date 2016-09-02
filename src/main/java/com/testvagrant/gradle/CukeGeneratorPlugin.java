package com.testvagrant.gradle;


import org.gradle.api.Project;
import org.gradle.api.Plugin;

public class CukeGeneratorPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getExtensions().create("cukeParallelPlugin", CukePluginExtension.class);
    }
}
