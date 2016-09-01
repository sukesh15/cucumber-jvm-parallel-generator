cucumber-jvm-parallel-plugin
============================

The project is motivated from a maven plugin in the similar name by
[temymers](https://github.com/temyers/cucumber-jvm-parallel-plugin) where he discusses how to generate multiple runners
per feature file to achieve cucumber-jvm parallel runs.

It was further enhanced by Sugat [here](https://github.com/sugatmankar/cucumber-jvm-parallel-plugin/tree/tagwiseOutlinewise)
to generate runners per test scenario.

This is porting the above into a gradle plugin. Teams that are using cucumber-jvm with gradle can use this to generate
runners per test scenario in their project. This plugin automatically generates a Cucumber JUnit or TestNG runner for
each scenario found in your project based on the tags that you want to run.

Usage
-----

Add the following to your build script

```buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.com.tv.gradle:CukeIT-Parallel-Generator:1.0-SNAPSHOT"
  }
}

apply plugin: "com.tv.gradle.cukeGenerator"```

And then you can add a task as below to genrate the runner classes

```task cukeGenerator(type: com.tv.gradle.GenerateTask) {
    cukeParallelPlugin.tags = "smoke"
    cukeParallelPlugin.outputDirectory = "${project.projectDir}/src/test/java/generated-test-sources/cucumber"
    cukeParallelPlugin.cucumberOutputDir = "${project.projectDir}/target/cucumber-parallel"
}```


