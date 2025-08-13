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
        // Creating a new list is necessary to return a type
        // List<Class> from the type List<Class<? extends PythonCheck>>
        List<Class<?>> checks = new ArrayList<>(PythonRuleList.getPythonChecks());
        LOG.info(
                "Registering PY checks for repo '{}' : {}",
                PythonScannerRuleDefinition.REPOSITORY_KEY,
                checks.stream()
                        .map(Class::getSimpleName)
                        .collect(Collectors.joining(", ")));
        return checks;
    }
}
