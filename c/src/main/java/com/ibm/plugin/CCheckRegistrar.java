package com.ibm.plugin;

import java.util.List;
import java.util.stream.Collectors;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.cxx.CustomCxxRulesDefinition;

public final class CCheckRegistrar extends CustomCxxRulesDefinition {

  private static final Logger LOG = Loggers.get(CCheckRegistrar.class);
  private static final String ORIGIN = CCheckRegistrar.class.getSimpleName() + ".java";

  /*@Override
  public String repositoryName() {
    return CScannerRuleDefinition.REPOSITORY_NAME;
  }

  @Override
  public String repositoryKey() {
    return CScannerRuleDefinition.REPOSITORY_KEY;
  }
*/
  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Class[] checkClasses() {
    List<Class<?>> checks = CRuleList.getChecks();
    LOG.info(
        "CXX {}: event=<register-checks> repoKey={} checks=[{}]",
        ORIGIN,
        //CScannerRuleDefinition.REPOSITORY_KEY,
        checks.stream().map(Class::getSimpleName).collect(Collectors.joining(",")));
    return checks.toArray(new Class[0]);
  }
}
