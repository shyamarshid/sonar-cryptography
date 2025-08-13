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
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.plugin;

import com.ibm.util.CryptoTrace;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

public class PythonScannerRuleDefinition implements RulesDefinition {
    private static final Logger LOG = Loggers.get(PythonScannerRuleDefinition.class);
    public static final String REPOSITORY_KEY = "sonar-python-crypto";
    public static final String REPOSITORY_NAME = "Sonar Cryptography";

    // Add the rule keys of the rules which need to be considered as template-rules
    private static final Set<String> RULE_TEMPLATES_KEY = Collections.emptySet();

    private static final String RESOURCE_BASE_PATH = "/org/sonar/l10n/python/rules/python";

    private final SonarRuntime sonarRuntime;

    public PythonScannerRuleDefinition(SonarRuntime sonarRuntime) {
        this.sonarRuntime = sonarRuntime;
    }

    @Override
    public void define(Context context) {
        if (CryptoTrace.isEnabled()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(
                        CryptoTrace.fmt(
                                this,
                                "define",
                                "start repo=" + REPOSITORY_KEY + " lang=py"));
            } else if (LOG.isDebugEnabled()) {
                LOG.debug(
                        CryptoTrace.fmt(
                                this,
                                "define",
                                "start repo=" + REPOSITORY_KEY + " lang=py"));
            }
        }
        NewRepository repository =
                context.createRepository(REPOSITORY_KEY, "py").setName(REPOSITORY_NAME);

        RuleMetadataLoader ruleMetadataLoader =
                new RuleMetadataLoader(RESOURCE_BASE_PATH, sonarRuntime);
        ruleMetadataLoader.addRulesByAnnotatedClass(repository, PythonRuleList.getChecks());
        setTemplates(repository);

        repository.done();
        if (CryptoTrace.isEnabled()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(
                        CryptoTrace.fmt(
                                this,
                                "define",
                                "end repo="
                                        + REPOSITORY_KEY
                                        + " lang=py rules="
                                        + PythonRuleList.getChecks().size()));
            } else if (LOG.isDebugEnabled()) {
                LOG.debug(
                        CryptoTrace.fmt(
                                this,
                                "define",
                                "end repo="
                                        + REPOSITORY_KEY
                                        + " lang=py rules="
                                        + PythonRuleList.getChecks().size()));
            }
        }
    }

    private static void setTemplates(NewRepository repository) {
        RULE_TEMPLATES_KEY.stream()
                .map(repository::rule)
                .filter(Objects::nonNull)
                .forEach(rule -> rule.setTemplate(true));
    }
}
