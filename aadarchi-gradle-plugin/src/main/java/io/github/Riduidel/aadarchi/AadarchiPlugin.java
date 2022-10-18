package io.github.Riduidel.aadarchi;

import groovy.lang.Closure;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.ndx.aadarchi.base.ArchitectureDocumentationBuilder;

import java.io.File;

public class AadarchiPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.task("aadarchi", it -> it.doLast(new Closure<Void>(null) {
            public Object doCall() throws Throwable {
                System.setProperty("project.build.directory",
                                project.getProjectDir().getAbsolutePath() + File.separator + "build");
                System.setProperty("project.basedir", project.getProjectDir().getAbsolutePath());
                ArchitectureDocumentationBuilder.main(null);
                return null;
            }
        }));
    }
}
