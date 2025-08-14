/*
 * Sonar Cryptography Plugin
 * Copyright (C) 2024 PQCA
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ibm.plugin;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;
import org.sonar.api.server.rule.RulesDefinition.NewRule;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.cxx.CxxLanguage;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

public final class CScannerRuleDefinition implements RulesDefinition {

  public static final String REPOSITORY_KEY = "sonar-c-crypto";
  public static final String REPOSITORY_NAME = "Sonar Cryptography";

  // Keep empty unless you truly have template rules
  private static final Set<String> RULE_TEMPLATES_KEY = Collections.emptySet();

  // Where rule metadata/HTML lives (mirrors python layout)
  // Expected files, for example:
  //   /org/sonar/l10n/cxx/rules/sonar-c-crypto/Inventory.html
  //   /org/sonar/l10n/cxx/rules/sonar-c-crypto/Inventory.json   (optional; provides title/etc.)
  private static final String RESOURCE_BASE_PATH =
      "/org/sonar/l10n/cxx/rules/sonar-c-crypto";

  private static final Logger LOG = Loggers.get(CScannerRuleDefinition.class);

  private final SonarRuntime sonarRuntime;

  public CScannerRuleDefinition(SonarRuntime sonarRuntime) {
    this.sonarRuntime = sonarRuntime;
  }

  @Override
  public void define(Context context) {
    NewRepository repository =
        context.createRepository(REPOSITORY_KEY, CxxLanguage.KEY).setName(REPOSITORY_NAME);

    // Load rule metadata from resources and annotated check classes
    // (annotation @Rule on CInventoryRule provides key/name; HTML provides description)
    RuleMetadataLoader loader = new RuleMetadataLoader(RESOURCE_BASE_PATH, sonarRuntime);
    loader.addRulesByAnnotatedClass(repository, CRuleList.getChecks());
    setTemplates(repository);

    repository.done();

    LOG.info(
        "CXX repo registered '{}' with rules {}",
        REPOSITORY_KEY,
        repository.rules().stream().map(NewRule::key).collect(Collectors.toList()));
  }

  private static void setTemplates(NewRepository repository) {
    RULE_TEMPLATES_KEY.stream()
        .map(repository::rule)
        .filter(Objects::nonNull)
        .forEach(rule -> rule.setTemplate(true));
  }
}
