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

```
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.com.tv.gradle:CukeIT-Parallel-Generator:1.0-SNAPSHOT"
  }
}

apply plugin: "com.tv.gradle.cukeGenerator"
```

And then you can add a task as below to genrate the runner classes

```
task cukeGenerator(type: GenerateTask)
{
    cukeParallelPlugin.tags = "smoke"
    cukeParallelPlugin.outputDirectory = "${project.projectDir}/src/test/java/generated-test-sources/cucumber"
    cukeParallelPlugin.cucumberOutputDir = "${project.projectDir}/target/cucumber-parallel"
}
```

Here is list of of all the properties and their default values for the GenerateTask, that can be overwritten.
-------------------------------------------------------------------------------------------------------------

```
featuresDirectory = "src/test/resources/features"
encoding = "UTF-8"
cucumberOutputDir = "target/cucumber-parallel"
useTestNG = false
namingScheme = "simple"
namingPattern = "Parallel{c}IT"
filterFeaturesByTags = false
filterScenarioAndOutlineByLines = true
outputDirectory = "${project.projectDir}/src/test/java/generated-test-sources/cucumber"
glue = "stepe"
tags = "@completed"
format = "json"
strict = true
monochrome = false
cucumberOptions = "cucumber.options"
useReRun = false
retryCount = 0
```


If `cucumber.options` VM argument is specified as per the [Cucumber CLI options](https://cucumber.io/docs/reference/jvm), they shall override the configuration tags.

Where glue is a comma separated list of package names to use for the Cucumber Glue.

The plugin will search `featuresDirectory` for `*.feature` files and generate a JUnit test for each one.
> **WARNING:** `featuresDirectory` must denote a directory within the root of the classpath.
> **Example:**
> * Resources in `src/test/resources` are added to the classpath by default.
> * `src/test/resources/features` **is** in the root of the classpath, so **would be valid** for `featuresDirectory`
> * `src/test/resources/features/sub_folder` is **not** in the root of the classpath, so **would not be valid** to put in `featuresDirectory`

If you prefer to generate TestNG tests, set `useTestNG` to true

The Java source is generated in `outputDirectory`, based on the naming scheme used.

Each runner is configured to output the results to a separate output file under `target/cucumber-parallel`

###Naming Scheme

The naming scheme used for the generated files is controlled by the `namingScheme` property.  The following values are supported:

| Property      | Generated Name |
| ------------- | -------------- |
| simple        | `ParallelXXIT.java`, where `XX` is a one up counter.
| feature-title | The name is generated based on the feature title with the following rules to ensure it is a valid classname:
* Spaces are removed, camel-casing the title.
* If the feature file starts with a digit, the classname is prefixed with '_'
* A on up counter is appended to the classname, to prevent clashes. |
| pattern       | Generate the filename based on the `namingPattern` property.
The following tokens can be used in the pattern:
* `{f}` Converts the feature file name to a valid class name using the rules for feature-title, apart from the one up counter.
* `{c}` Adds a one up counter. |

By default, generated test files use the `simple` naming strategy.

####Note on Pattern Naming Scheme
The `pattern` naming scheme is for advanced usage only.

It is up to you to ensure that class names generated are valid and there are no clashes.  If the same class name is generated multiple times, then it shall be overwritten and some of your tests will not be executed.

The `namingPattern` property is for the **class name** only.  Do not add the `.java` suffix.

###Re-Run Functionality

1. **What it does?**<br>
It re-run only failed test cases on each run and after complete run it generate consolidated report. Max retry count allowed is 5
2. **How to enable it?** </br>
update the related properties in the gradle task configuration
```
useReRun=true
retryCount=1
```


