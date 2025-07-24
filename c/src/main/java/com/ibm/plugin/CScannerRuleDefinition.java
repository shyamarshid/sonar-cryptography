package com.ibm.plugin;

import java.util.Objects;
import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.cxx.CxxLanguage;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

public class CScannerRuleDefinition implements RulesDefinition {
    public static final String REPOSITORY_KEY = "c";
    public static final String REPOSITORY_NAME = "Sonar Cryptography";
    private static final String RESOURCE_BASE_PATH = "/org/sonar/l10n/c/rules/c";

    private final SonarRuntime sonarRuntime;

    public CScannerRuleDefinition(SonarRuntime sonarRuntime) {
        this.sonarRuntime = sonarRuntime;
    }

    @Override
    public void define(Context context) {
        NewRepository repository =
                context.createRepository(REPOSITORY_KEY, CxxLanguage.KEY).setName(REPOSITORY_NAME);

        RuleMetadataLoader loader = new RuleMetadataLoader(RESOURCE_BASE_PATH, this.sonarRuntime);
        loader.addRulesByAnnotatedClass(repository, CRuleList.getChecks());
        repository.done();
    }
}
