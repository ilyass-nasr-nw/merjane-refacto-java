package com.nimbleways.merjane.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.nimbleways.merjane");

    @Test
    public void controllers_shouldOnlyDependOn_servicesMessagingAndDomain() {
        ArchRule rule = classes()
                .that().resideInAPackage("..controllers..")
                .should().onlyDependOnClassesThat(
                        JavaClass.Predicates.resideOutsideOfPackage("com.nimbleways.merjane..")
                                .or(JavaClass.Predicates.resideInAnyPackage(
                                        "..controllers..",
                                        "..services..",
                                        "..messaging..",
                                        "..domain.."
                                ))
                );

        rule.check(classes);
    }

    @Test
    public void services_shouldOnlyDependOn_persistenceAndDomain() {
        ArchRule rule = classes()
                .that().resideInAPackage("..services..")
                .should().onlyDependOnClassesThat(
                        JavaClass.Predicates.resideOutsideOfPackage("com.nimbleways.merjane..")
                                .or(JavaClass.Predicates.resideInAnyPackage(
                                        "..services..",
                                        "..persistence..",
                                        "..domain.."
                                ))
                );

        rule.check(classes);
    }
}
