package com.ibm.plugin;

import java.util.List;
import org.sonar.plugins.cxx.CustomCxxRulesDefinition;
import org.sonarsource.api.sonarlint.SonarLintSide;

@SonarLintSide
public final class CCheckRegistrar extends CustomCxxRulesDefinition {

  @Override
  public String repositoryName() {
    return CScannerRuleDefinition.REPOSITORY_NAME;
  }

  @Override
  public String repositoryKey() {
    return CScannerRuleDefinition.REPOSITORY_KEY;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Class[] checkClasses() {
    List<Class<?>> checks = CRuleList.getChecks();
    return checks.toArray(new Class[0]);
  }
}
