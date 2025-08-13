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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.python.api.PythonCustomRuleRepository;
import org.sonarsource.api.sonarlint.SonarLintSide;

@SonarLintSide
public class PythonCheckRegistrar implements PythonCustomRuleRepository {
    private static final Logger LOG = Loggers.get(PythonCheckRegistrar.class);

    @Override
    public String repositoryKey() {
        return PythonScannerRuleDefinition.REPOSITORY_KEY;
    }

    @Override
    public List<Class<?>> checkClasses() {
        List<Class<?>> checks = new ArrayList<>(PythonRuleList.getPythonChecks());
        if (CryptoTrace.isEnabled()) {
            String names =
                    checks.stream().map(Class::getSimpleName).collect(Collectors.joining(","));
            if (LOG.isTraceEnabled()) {
                LOG.trace(
                        CryptoTrace.fmt(
                                this,
                                "checkClasses",
                                "repo=" + repositoryKey() + " checks=" + names));
            } else if (LOG.isDebugEnabled()) {
                LOG.debug(
                        CryptoTrace.fmt(
                                this,
                                "checkClasses",
                                "repo=" + repositoryKey() + " checks=" + names));
            }
        }
        return checks;
    }
}
