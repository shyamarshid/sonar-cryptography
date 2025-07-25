package com.ibm.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.sonar.api.SonarRuntime;
import org.sonar.api.rules.RuleType; // this one still exists
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;
import org.sonar.api.server.rule.RulesDefinition.NewRule;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.cxx.CxxLanguage;

public final class CScannerRuleDefinition implements RulesDefinition {

  private static final Logger LOG = Loggers.get(CScannerRuleDefinition.class);

  /** Keep these public – CCheckRegistrar uses them */
  public static final String REPOSITORY_KEY  = "c";
  public static final String REPOSITORY_NAME = "Sonar Cryptography";

  private static final String INVENTORY_KEY  = "Inventory";
  private static final String INVENTORY_HTML = "org/sonar/l10n/c/rules/c/Inventory.html";

  private final SonarRuntime runtime;

  public CScannerRuleDefinition(SonarRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public void define(Context context) {
    NewRepository repo = context
        .createRepository(REPOSITORY_KEY, CxxLanguage.KEY)
        .setName(REPOSITORY_NAME);

    String html = readResource(INVENTORY_HTML);

    NewRule rule = repo.createRule(INVENTORY_KEY)
        .setName("Cryptographic Inventory (CBOM)")
        .setHtmlDescription(html)
        .setType(RuleType.CODE_SMELL)
        // Use String to avoid importing org.sonar.api.rule.Severity
        .setSeverity("MINOR")
        .setTags("cryptography", "cbom", "cwe");

    repo.done();

    LOG.info("Registered C repository '{}' with rule '{}'", REPOSITORY_KEY, rule.key());
  }

  private static String readResource(String path) {
    try (InputStream in = CScannerRuleDefinition.class.getClassLoader().getResourceAsStream(path)) {
      if (in == null) {
        LOG.warn("HTML description not found on classpath: {}", path);
        return "<h2>Cryptographic Inventory (CBOM)</h2>";
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      LOG.warn("Could not load HTML description for {}", path, e);
      return "<h2>Cryptographic Inventory (CBOM)</h2>";
    }
  }
}
