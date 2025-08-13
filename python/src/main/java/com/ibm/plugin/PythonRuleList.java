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

import com.ibm.plugin.rules.PythonInventoryRule;
import com.ibm.plugin.rules.PythonNoMD5UseRule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.python.api.PythonCheck;

public final class PythonRuleList {

    private PythonRuleList() {}

    private static final Logger LOG = Loggers.get(PythonRuleList.class);

    public static @Nonnull List<Class<?>> getChecks() {
        List<Class<? extends PythonCheck>> checks = new ArrayList<>();
        checks.addAll(getPythonChecks());
        checks.addAll(getPythonTestChecks());
        List<Class<?>> result = Collections.unmodifiableList(checks);
        LOG.info(
                "PY rule list -> {} checks: {}",
                result.size(),
                result.stream()
                        .map(Class::getSimpleName)
                        .collect(Collectors.joining(", ")));
        return result;
    }

    /** These rules are going to target MAIN code only */
    public static @Nonnull List<Class<? extends PythonCheck>> getPythonChecks() {
        List<Class<? extends PythonCheck>> list =
                List.of(PythonInventoryRule.class, PythonNoMD5UseRule.class);
        LOG.info(
                "PY rule list -> {} checks: {}",
                list.size(),
                list.stream()
                        .map(Class::getSimpleName)
                        .collect(Collectors.joining(", ")));
        return list;
    }

    /** These rules are going to target TEST code only */
    public static List<Class<? extends PythonCheck>> getPythonTestChecks() {
        List<Class<? extends PythonCheck>> list = List.of();
        LOG.info(
                "PY rule list -> {} checks: {}",
                list.size(),
                list.stream()
                        .map(Class::getSimpleName)
                        .collect(Collectors.joining(", ")));
        return list;
    }
}
