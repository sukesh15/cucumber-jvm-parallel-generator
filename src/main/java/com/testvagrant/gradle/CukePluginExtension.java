package com.testvagrant.gradle;

public class CukePluginExtension {

    private boolean useReRun;

    private String featuresDirectory = "src/test/java/features/";
    private String encoding = "UTF-8";
    private String cucumberOutputDir = "target/cucumber-parallel";
    private boolean useTestNG = false;
    private String namingScheme = "simple";
    private String namingPattern = "Parallel{c}IT";
    private boolean filterFeaturesByTags = false;
    private boolean filterScenarioAndOutlineByLines = true;
    private String outputDirectory = "${project.build.directory}/generated-test-sources/cucumber";
    private String glue = "steps";
    private String tags;
    private String format = "json";
    private boolean strict = true;
    private boolean monochrome = false;
    private String cucumberOptions = "cucumber.options";
    private int retryCount = 0;

    public boolean isFilterScenarioAndOutlineByLines() {
        return filterScenarioAndOutlineByLines;
    }

    public void setFilterScenarioAndOutlineByLines(boolean filterScenarioAndOutlineByLines) {
        this.filterScenarioAndOutlineByLines = filterScenarioAndOutlineByLines;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public boolean isUseReRun() {
        return useReRun;
    }

    public void setUseReRun(boolean useReRun) {
        this.useReRun = useReRun;
    }

    public String getFeaturesDirectory() {
        return featuresDirectory;
    }

    public void setFeaturesDirectory(String featuresDirectory) {
        this.featuresDirectory = featuresDirectory;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getCucumberOutputDir() {
        return cucumberOutputDir;
    }

    public void setCucumberOutputDir(String cucumberOutputDir) {
        this.cucumberOutputDir = cucumberOutputDir;
    }

    public boolean isUseTestNG() {
        return useTestNG;
    }

    public void setUseTestNG(boolean useTestNG) {
        this.useTestNG = useTestNG;
    }

    public String getNamingScheme() {
        return namingScheme;
    }

    public void setNamingScheme(String namingScheme) {
        this.namingScheme = namingScheme;
    }

    public String getNamingPattern() {
        return namingPattern;
    }

    public void setNamingPattern(String namingPattern) {
        this.namingPattern = namingPattern;
    }

    public boolean isFilterFeaturesByTags() {
        return filterFeaturesByTags;
    }

    public void setFilterFeaturesByTags(boolean filterFeaturesByTags) {
        this.filterFeaturesByTags = filterFeaturesByTags;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getGlue() {
        return glue;
    }

    public void setGlue(String glue) {
        this.glue = glue;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public boolean isMonochrome() {
        return monochrome;
    }

    public void setMonochrome(boolean monochrome) {
        this.monochrome = monochrome;
    }

    public String getCucumberOptions() {
        return cucumberOptions;
    }

    public void setCucumberOptions(String cucumberOptions) {
        this.cucumberOptions = cucumberOptions;
    }
}
