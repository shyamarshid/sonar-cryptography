package com.ibm.plugin;

import java.net.URL;
import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.NewRule;
import org.sonar.plugins.cxx.CxxLanguage;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class CScannerRuleDefinition implements RulesDefinition {

  private static final Logger LOG = Loggers.get(CScannerRuleDefinition.class);

  public static final String REPOSITORY_KEY = "c";
  public static final String REPOSITORY_NAME = "Sonar Cryptography";
  // no leading slash
  private static final String RESOURCE_BASE_PATH = "org/sonar/l10n/c/rules/c";

  private final SonarRuntime sonarRuntime;

  public CScannerRuleDefinition(SonarRuntime sonarRuntime) {
    this.sonarRuntime = sonarRuntime;
  }

  @Override
  public void define(Context context) {
    // sanity check: are the metadata files on the classpath?
    URL json = getClass().getClassLoader()
        .getResource(RESOURCE_BASE_PATH + "/Inventory.json");
    URL html = getClass().getClassLoader()
        .getResource(RESOURCE_BASE_PATH + "/Inventory.html");
    LOG.info("C rule metadata on classpath? json={}, html={}", json, html);

    NewRepository repo =
        context.createRepository(REPOSITORY_KEY, CxxLanguage.KEY)
               .setName(REPOSITORY_NAME);

    RuleMetadataLoader loader = new RuleMetadataLoader(RESOURCE_BASE_PATH, sonarRuntime);
    loader.addRulesByAnnotatedClass(repo, CRuleList.getChecks());

    // Hard fallback so SonarQube can't crash even if JSON wasn't picked up
    for (NewRule r : repo.rules()) {
      r.setName(r.key());
    }
    NewRule inv = repo.rule("Inventory");
    if (inv != null) {
      inv.setName("Cryptographic Inventory (CBOM)");
    }

    repo.done();
  }
}
