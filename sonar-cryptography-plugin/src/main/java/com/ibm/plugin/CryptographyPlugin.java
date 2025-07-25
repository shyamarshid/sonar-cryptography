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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.Plugin;
import org.sonar.api.SonarProduct;
import org.sonar.api.SonarRuntime;
import com.ibm.plugin.CCheckRegistrar;
import com.ibm.plugin.CScannerRuleDefinition;

public class CryptographyPlugin implements Plugin {

    @SuppressWarnings({"java:S1874"})
    private static final Logger LOGGER = LoggerFactory.getLogger(CryptographyPlugin.class);

    @Override
    public void define(Context context) {
        SonarRuntime runtime = context.getRuntime();
        SonarProduct product = runtime.getProduct();

        LOGGER.info("Sonar Cryptography initialized in context (" + product + ")");

        context.addExtensions(Configuration.getPropertyDefinitions()); // add configuration
        context.addExtensions(
                // java
                JavaScannerRuleDefinition.class, // Define Rules
                JavaCheckRegistrar.class, // Register Java rules by sonar-java sensor
                // python
                PythonScannerRuleDefinition.class, // Define Rules
                PythonCheckRegistrar.class, // Register Python rules by sonar-python sensor
                // c/c++
                CScannerRuleDefinition.class,
                //CCheckRegistrar.class,
                // general
                OutputFileJob.class);
    }
}
