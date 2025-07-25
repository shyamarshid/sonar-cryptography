package com.ibm.plugin;

import java.net.URL;
import java.util.stream.Collectors;
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
  private static final String RESOURCE_BASE_PATH = "/org/sonar/l10n/c/rules/c";

  private final SonarRuntime sonarRuntime;

  public CScannerRuleDefinition(SonarRuntime sonarRuntime) {
    this.sonarRuntime = sonarRuntime;
  }

  @Override
  public void define(Context context) {
    // Sanity check: can we see the metadata files?
    ClassLoader cl = getClass().getClassLoader();
    URL json = cl.getResource(RESOURCE_BASE_PATH.substring(1) + "/Inventory.json");
    URL html = cl.getResource(RESOURCE_BASE_PATH.substring(1) + "/Inventory.html");
    LOG.info("C rule metadata on classpath? json={}, html={}", json, html);

    NewRepository repo =
        context.createRepository(REPOSITORY_KEY, CxxLanguage.KEY)
               .setName(REPOSITORY_NAME);

    RuleMetadataLoader loader = new RuleMetadataLoader(RESOURCE_BASE_PATH, sonarRuntime);
    loader.addRulesByAnnotatedClass(repo, CRuleList.getChecks());

    // Log the keys we actually loaded
    LOG.info("Loaded C rule keys: {}", repo.rules().stream()
        .map(NewRule::key).collect(Collectors.toList()));

    // Hard fallback: guarantee every rule has a name
    for (NewRule r : repo.rules()) {
      r.setName(r.key());
    }

    // Explicitly set Inventory’s name too
    NewRule inv = repo.rule("Inventory");
    if (inv == null) {
      LOG.warn("Rule 'Inventory' not found in repository '{}'", REPOSITORY_KEY);
    } else {
      inv.setName("Cryptographic Inventory (CBOM)");
    }

    repo.done();
  }
}
