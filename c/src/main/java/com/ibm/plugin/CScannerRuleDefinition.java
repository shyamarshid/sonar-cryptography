package com.ibm.plugin;

import java.nio.charset.StandardCharsets;

import org.sonar.api.SonarRuntime;
import org.sonar.api.rules.RuleStatus;
import org.sonar.api.rules.RuleType;
import org.sonar.api.rules.Severity;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;
import org.sonar.api.server.rule.RulesDefinition.NewRule;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.cxx.CxxLanguage;
import org.sonarsource.analyzer.commons.Resources;

public final class CScannerRuleDefinition implements RulesDefinition {

  private static final Logger LOG = Loggers.get(CScannerRuleDefinition.class);

  private static final String REPOSITORY_KEY  = "c";
  private static final String REPOSITORY_NAME = "Sonar Cryptography";
  private static final String INVENTORY_KEY   = "Inventory";
  private static final String INVENTORY_HTML  =
      "org/sonar/l10n/c/rules/c/Inventory.html";

  private final SonarRuntime runtime;

  public CScannerRuleDefinition(SonarRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public void define(Context context) {
    NewRepository repo = context
        .createRepository(REPOSITORY_KEY, CxxLanguage.KEY)
        .setName(REPOSITORY_NAME);

    String html = readHtml(INVENTORY_HTML);

    NewRule rule = repo.createRule(INVENTORY_KEY)
        .setName("Cryptographic Inventory (CBOM)")
        .setHtmlDescription(html)
        .setType(RuleType.CODE_SMELL)
        .setSeverity(Severity.MINOR)
        .setStatus(RuleStatus.READY)
        .setTags("cryptography", "cbom", "cwe");

    repo.done();
    LOG.info("Registered C repository '{}' with rule key '{}'", REPOSITORY_KEY, rule.key());
  }

  private static String readHtml(String resourcePath) {
    try {
      return Resources.toString(
          CScannerRuleDefinition.class.getClassLoader().getResource(resourcePath),
          StandardCharsets.UTF_8);
    } catch (Exception e) {
      LOG.warn("Could not load HTML description for {}", resourcePath, e);
      return "<h2>Cryptographic Inventory (CBOM)</h2>";
    }
  }
}
