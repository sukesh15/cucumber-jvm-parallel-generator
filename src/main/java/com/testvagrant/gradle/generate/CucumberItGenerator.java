package com.testvagrant.gradle.generate;



import com.testvagrant.gradle.CukePluginExtension;
import org.gradle.api.tasks.TaskExecutionException;
import com.testvagrant.gradle.generate.name.ClassNamingScheme;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
//import org.apache.maven.plugin.MojoExecutionException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.TokenMatcher;
import gherkin.ast.*;

import java.io.*;
import java.util.*;

public class CucumberItGenerator {

    private final CukePluginExtension extension;
    private final OverriddenCucumberOptionsParameters overriddenParameters;
    private final OverriddenRerunOptionsParameters overriddenRerunOptionsParameters;
    private final ClassNamingScheme classNamingScheme;
    int fileCounter = 1;
    private String featureFileLocation;
    private Template velocityTemplate;
    private String outputFileName;
    private String htmlFormat;
    private String jsonFormat;
    private String rerunFormat;

    public CucumberItGenerator(final CukePluginExtension extension,
                               final OverriddenCucumberOptionsParameters overriddenParameters,
                               final ClassNamingScheme classNamingScheme,
                               final OverriddenRerunOptionsParameters overriddenRerunOptionsPrms) {
        this.extension = extension;
        this.overriddenParameters = overriddenParameters;
        this.classNamingScheme = classNamingScheme;
        this.overriddenRerunOptionsParameters = overriddenRerunOptionsPrms;
        initTemplate();
    }

    private void initTemplate() {
        final Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        final VelocityEngine engine = new VelocityEngine(props);
        engine.init();

        if (extension.isUseTestNG()) {
            velocityTemplate = engine.getTemplate("cucumber-testng-runner.vm",
                    extension.getEncoding());
        } else if (extension.isUseReRun()) {
            velocityTemplate = engine.getTemplate("cucumber-junit-re-runner.vm",
                    extension.getEncoding());
        } else {
            velocityTemplate = engine.getTemplate("cucumber-junit-runner.vm",
                    extension.getEncoding());
        }
    }

    public void generateCucumberItFiles(final File outputDirectory)
            throws TaskExecutionException {

        Collection<File> featureFiles = new ArrayList<File>();
        if (overriddenParameters.getFeaturePaths().size() != 0) {
            for (final String f : overriddenParameters.getFeaturePaths()) {
                featureFiles.add(new File(f));
            }
        } else {
            featureFiles = FileUtils.listFiles(new File(extension.getFeaturesDirectory()),
                    new String[]{"feature"}, true);
        }
        List<String> parsedTags = new ArrayList<String>();
        System.out.println("Tags -- " + overriddenParameters.getTags());
        String[] allTags = overriddenParameters.getTags().split(",");
        // length is 1 because of --tags in RuntimeOptions
        if (allTags.length == 1 && allTags[0].equals("--tags")) {
            parsedTags.addAll(getAllTagsFromAllFeatureFiles());
        } else {
            for (final String t : allTags) {
                parsedTags.add(t.replaceAll("\"", "").replaceAll("\\s+", ""));
            }
        }

        for (final String tag : parsedTags) {
            for (final File file : featureFiles) {
                try {
                    Parser<GherkinDocument> parser = new Parser<>(new AstBuilder());
                    GherkinDocument gherkinDocument = null;
                    try {
                        gherkinDocument = parser.parse(new FileReader(file),
                                new TokenMatcher());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    List<Tag> featureTags = gherkinDocument.getFeature().getTags();
                    if (extension.isFilterFeaturesByTags()) {
                        for (final Tag t : featureTags) {
                            if (tag.startsWith("~") && ("~" + t.getName()).equals(tag)) {
                                // Don't plugin.com.tv.gradle.generate file
                            }
                            if (t.getName().equals(tag)) {
                                setFeatureFileLocation(file);
                                generateItFiles(tag, file.getName(), outputDirectory);
                            }
                        }
                    } else {
                        List<ScenarioDefinition> definitions = gherkinDocument.getFeature()
                                .getChildren();
                        for (final ScenarioDefinition definition : definitions) {
                            if (definition instanceof ScenarioOutline) {
                                ScenarioOutline scenarioOutline = (ScenarioOutline) definition;
                                List<Tag> outlineTags = scenarioOutline.getTags();
                                for (final Tag t : outlineTags) {
                                    if (tag.startsWith("~") && ("~" + t.getName())
                                            .equals(tag)) {
                                        // Don't plugin.com.tv.gradle.generate file
                                    }
                                    if (t.getName().equals(tag)) {
                                        if (extension.isFilterScenarioAndOutlineByLines()) {
                                            List<Examples> examples = scenarioOutline.getExamples();
                                            for (final Examples example : examples) {
                                                List<TableRow> tableBody = example.getTableBody();
                                                for (final TableRow tableRow : tableBody) {
                                                    setScenarioOutlineLocation(
                                                            file.getPath()
                                                                    .substring(file.getPath()
                                                                            .indexOf(
                                                                                    new File(
                                                                                            extension
                                                                                                    .getFeaturesDirectory())
                                                                                            .getPath()))
                                                                    + ":" + tableRow
                                                                    .getLocation().getLine());
                                                    generateItFiles(tag, file.getName(),
                                                            outputDirectory);
                                                }
                                            }
                                        } else {
                                            setFeatureFileLocation(file);
                                            generateItFiles(tag, file.getName(), outputDirectory);
                                        }
                                    }
                                }
                            }
                            if (definition instanceof Scenario) {
                                Scenario scenario = (Scenario) definition;
                                List<Tag> scenarioTags = scenario.getTags();
                                for (final Tag t : scenarioTags) {
                                    if (tag.startsWith("~") && ("~" + t.getName())
                                            .equals(tag)) {
                                        // Don't plugin.com.tv.gradle.generate file
                                    }
                                    if (t.getName().equals(tag)) {
                                        if (extension.isFilterScenarioAndOutlineByLines()) {
                                            setScenarioOutlineLocation(
                                                    file.getPath()
                                                            .substring(file.getPath()
                                                                    .indexOf(
                                                                            new File(
                                                                                    extension
                                                                                            .getFeaturesDirectory())
                                                                                    .getPath()))
                                                            + ":" + scenario.getLocation().getLine());
                                            generateItFiles(tag, file.getName(), outputDirectory);
                                        } else {
                                            setFeatureFileLocation(file);
                                            generateItFiles(tag, file.getName(), outputDirectory);
                                        }
                                    }
                                }
                            }
                        }
                    }

                } catch (final Exception e) {
                    throw new RuntimeException(
                            "Failed to read contents of " + file.getPath()
                                    + ". Check Feature file syntax errors and make sure that Scenario or "
                                    + "Scenario outline are "
                                    + "tagged with at "
                                    + "least one tag.");
                }
            }
        }

    }

    private void generateItFiles(final String tag, final String fileName, final File
            outputDirectory)
            throws TaskExecutionException {
        outputFileName = classNamingScheme.generate(fileName);
        final File outputFile = new File(outputDirectory, outputFileName + ".java");
        FileWriter writer = null;
        try {
            writer = new FileWriter(outputFile);
            writeContentFromTemplate(writer, tag);
        } catch (final IOException e) {
            throw new RuntimeException("Error creating file "
                    + outputFile, e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (final IOException e) {
                    // ignore
                }
            }
        }
        fileCounter++;
    }

    /**
     * Sets the feature file location based on the given file. The full file path is trimmed to only
     * include the featuresDirectory. E.g. /myproject/src/test/resources/features/feature1.feature
     * will be saved as features/feature1.feature
     *
     * @param file The feature file
     */
    private void setFeatureFileLocation(final File file) {
        featureFileLocation = file.getPath().substring(file.getPath().indexOf(
                new File(extension.getFeaturesDirectory()).getPath()))
                .replace(File.separatorChar, '/');
    }

    private void setScenarioOutlineLocation(final String outLine) {
        featureFileLocation = outLine.replace(File.separatorChar, '/');
    }

    private void writeContentFromTemplate(final Writer writer, final String tag) {

        final VelocityContext context = new VelocityContext();
        context.put("strict", overriddenParameters.isStrict());
        context.put("featureFile", featureFileLocation);
        context.put("flagSOutline", extension.isFilterScenarioAndOutlineByLines());
        context.put("reports", createFormatStrings());
        context.put("tags", "\"" + tag + "\"");
        context.put("monochrome", overriddenParameters.isMonochrome());
        context.put("cucumberOutputDir", extension.getCucumberOutputDir());
        if (extension.isUseReRun()) {
            context.put("glue", overriddenParameters.getGlue());
        } else {
            context.put("glue", quoteGlueStrings());
        }
        context.put("className", FilenameUtils.removeExtension(outputFileName));
        context.put(
                "outPutPath",
                extension.getCucumberOutputDir().replace('\\', '/') + "/"
                        + FilenameUtils.removeExtension(outputFileName) + "/"
                        + FilenameUtils.removeExtension(outputFileName));
        context.put("retryCount", overriddenRerunOptionsParameters.getRetryCount());
        context.put("htmlFormat", this.htmlFormat);
        context.put("jsonFormat", this.jsonFormat);
        context.put("rerunFormat", this.rerunFormat);
        velocityTemplate.merge(context, writer);
    }

    /**
     * Create the format string used for the output.
     */
    private String createFormatStrings() {
        final String[] formatStrs = overriddenParameters.getFormat().split(",");

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < formatStrs.length; i++) {
            final String formatStr = formatStrs[i].trim();

            if (extension.isUseReRun()) {
                sb.append(String.format("\"%s:%s/%s/%s.%s\"",
                        formatStr,
                        extension.getCucumberOutputDir().replace('\\', '/'),
                        FilenameUtils.removeExtension(outputFileName),
                        FilenameUtils.removeExtension(outputFileName),
                        formatStr));
                if (formatStr.equalsIgnoreCase("html")) {
                    htmlFormat = String.format("\"%s:%s/%s/%s.%s\"",
                            formatStr,
                            extension.getCucumberOutputDir().replace('\\', '/'),
                            FilenameUtils.removeExtension(outputFileName),
                            FilenameUtils.removeExtension(outputFileName),
                            formatStr);
                }
                if (formatStr.equalsIgnoreCase("json")) {
                    jsonFormat = String.format("\"%s:%s/%s/%s.%s\"",
                            formatStr,
                            extension.getCucumberOutputDir().replace('\\', '/'),
                            FilenameUtils.removeExtension(outputFileName),
                            FilenameUtils.removeExtension(outputFileName),
                            formatStr);
                }
                if (formatStr.equalsIgnoreCase("rerun")) {
                    rerunFormat = String.format("\"%s:%s/%s/%s.%s\"",
                            formatStr,
                            extension.getCucumberOutputDir().replace('\\', '/'),
                            FilenameUtils.removeExtension(outputFileName),
                            FilenameUtils.removeExtension(outputFileName),
                            "txt");
                }

            } else {
                sb.append(String.format("\"%s:%s/%s.%s\"",
                        formatStr,
                        extension.getCucumberOutputDir().replace('\\', '/'),
                        fileCounter,
                        formatStr));
            }
            if (i < formatStrs.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * Wraps each package in quotes for use in the template.
     */
    private String quoteGlueStrings() {
        final String[] packageStrs = overriddenParameters.getGlue().split(",");

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < packageStrs.length; i++) {
            final String packageStr = packageStrs[i];
            sb.append(String.format("\"%s\"", packageStr.trim()));

            if (i < packageStrs.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private List<String> getAllTagsFromAllFeatureFiles() {
        List<String> tags = new ArrayList<String>();
        Collection<File> featureFiles = new ArrayList<File>();
        featureFiles = FileUtils.listFiles(new File(extension.getFeaturesDirectory()),
                new String[]{"feature"}, true);
        for (final File file : featureFiles) {
            try {
                Parser<GherkinDocument> parser = new Parser<>(new AstBuilder());
                GherkinDocument gherkinDocument = null;
                try {
                    gherkinDocument = parser.parse(new FileReader(file),
                            new TokenMatcher());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                List<Tag> featureTags = gherkinDocument.getFeature().getTags();
                if (extension.isFilterFeaturesByTags()) {
                    tags.add(featureTags.get(0).getName());
                } else {
                    List<ScenarioDefinition> definitions = gherkinDocument.getFeature()
                            .getChildren();
                    for (final ScenarioDefinition definition : definitions) {
                        if (definition instanceof ScenarioOutline) {
                            ScenarioOutline scenarioOutline = (ScenarioOutline) definition;
                            List<Tag> outlineTags = scenarioOutline.getTags();
                            tags.add(outlineTags.get(0).getName());
                        }
                        if (definition instanceof Scenario) {
                            Scenario scenario = (Scenario) definition;
                            List<Tag> scenarioTags = scenario.getTags();
                            tags.add(scenarioTags.get(0).getName());
                        }
                    }
                }

            } catch (final Exception e) {
                throw new RuntimeException(
                        "Failed to read contents of " + file.getPath()
                                + ". Check Feature file syntax errors and make sure that Scenario or "
                                + "Scenario outline are "
                                + "tagged with at "
                                + "least one tag.");
            }
        }
        Set<String> sUniqueElement = new HashSet<String>();
        sUniqueElement.addAll(tags);
        tags.clear();
        tags.addAll(sUniqueElement);
        return tags;
    }

}
