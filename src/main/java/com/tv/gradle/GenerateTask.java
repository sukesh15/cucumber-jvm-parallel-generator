package com.tv.gradle;

import com.tv.gradle.generate.OverriddenCucumberOptionsParameters;
import com.tv.gradle.generate.OverriddenRerunOptionsParameters;
import com.tv.gradle.generate.name.OneUpCounter;
import com.tv.gradle.generate.CucumberItGenerator;
import com.tv.gradle.generate.name.ClassNamingScheme;
import com.tv.gradle.generate.name.ClassNamingSchemeFactory;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class GenerateTask extends DefaultTask {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    CucumberItGenerator fileGenerator;

    @TaskAction
    public void generateCukePluginTasks() throws TaskExecutionException {
        log.info("Starting generator task");

        try {
            CukePluginExtension extension = getProject().getExtensions().findByType(CukePluginExtension.class);
            File outputDirectory = extension.getOutputDirectory();
            String featuresDirectory = extension.getFeaturesDirectory();

//            if (!new File(featuresDirectory).exists()) {
//                throw new TaskExecutionException("Features directory does not exist");
//            }
            if (extension.getRetryCount() > 0) {
                extension.setUseReRun(true);
            }

            recreateOutputDirectory(extension);

            final OverriddenCucumberOptionsParameters overriddenParameters =
                    overrideParametersWithCucumberOptions(extension);

            final OverriddenRerunOptionsParameters rerunOptionsParameters =
                    overriddenRerunOptionsParameters(extension);

            final ClassNamingSchemeFactory factory =
                    new ClassNamingSchemeFactory(new OneUpCounter());

            final ClassNamingScheme classNamingScheme =
                    factory.create(extension.getNamingScheme(), extension.getNamingPattern());

            fileGenerator = new CucumberItGenerator(extension,
                    overriddenParameters,
                    classNamingScheme,
                    rerunOptionsParameters);

            fileGenerator.generateCucumberItFiles(outputDirectory);


//            project.addTestCompileSourceRoot(outputDirectory.getAbsolutePath());


            log.info("Successfully completed generator task");
        } catch (Exception e) {
            log.error("", e);
            throw new TaskExecutionException(this, new Exception("Exception occured while processing sampleTask", e));
        }

    }


    private void recreateOutputDirectory(CukePluginExtension extension) {
        if (extension.getOutputDirectory().exists()) {
            File[] files = extension.getOutputDirectory().listFiles();
            for (File file : files) {
                file.delete();
            }
            extension.getOutputDirectory().delete();
        }
        extension.getOutputDirectory().mkdirs();
    }

    /**
     * Overrides the parameters with cucumber.options if they have been
     * specified. Currently only tags are supported.
     *
     * @param extension
     * @return
     */
    private OverriddenCucumberOptionsParameters overrideParametersWithCucumberOptions(CukePluginExtension extension) {

        final OverriddenCucumberOptionsParameters overriddenParameters =
                new OverriddenCucumberOptionsParameters();
        overriddenParameters.setTags(extension.getTags()).setGlue(extension.getGlue()).setStrict(extension.isStrict())
                .setFormat(extension.getFormat()).setMonochrome(extension.isMonochrome());
        if (extension.isUseReRun()) {
            overriddenParameters.setFormat("json,html,rerun");
        } else {
            overriddenParameters.setFormat(extension.getFormat());
        }

        overriddenParameters.overrideParametersWithCucumberOptions(extension.getCucumberOptions());
        return overriddenParameters;

    }

    private OverriddenRerunOptionsParameters overriddenRerunOptionsParameters(CukePluginExtension extension) {
        final OverriddenRerunOptionsParameters rerunOptionsParams =
                new OverriddenRerunOptionsParameters();
        rerunOptionsParams.setRetryCount(extension.getRetryCount());
        return rerunOptionsParams;
    }
}
