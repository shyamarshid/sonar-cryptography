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
package com.ibm.plugin.rules.detection;

import com.ibm.common.IObserver;
import com.ibm.engine.detection.Finding;
import com.ibm.engine.executive.DetectionExecutive;
import com.ibm.engine.language.python.PythonScanContext;
import com.ibm.engine.rule.IDetectionRule;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.reorganizer.IReorganizerRule;
import com.ibm.plugin.PythonAggregator;
import com.ibm.plugin.translation.PythonTranslationProcess;
import com.ibm.plugin.translation.reorganizer.PythonReorganizerRules;
import com.ibm.rules.IReportableDetectionRule;
import com.ibm.rules.issue.Issue;
import com.ibm.util.CryptoTrace;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.python.api.PythonCheck;
import org.sonar.plugins.python.api.PythonVisitorCheck;
import org.sonar.plugins.python.api.PythonVisitorContext;
import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.Tree;

public abstract class PythonBaseDetectionRule extends PythonVisitorCheck
        implements IObserver<Finding<PythonCheck, Tree, Symbol, PythonVisitorContext>>,
                IReportableDetectionRule<Tree> {

    private static final Logger LOG = Loggers.get(PythonBaseDetectionRule.class);

    private final boolean isInventory;
    @Nonnull protected final PythonTranslationProcess pythonTranslationProcess;
    @Nonnull protected final List<IDetectionRule<Tree>> detectionRules;

    protected PythonBaseDetectionRule() {
        this.isInventory = false;
        this.detectionRules = PythonDetectionRules.rules();
        this.pythonTranslationProcess =
                new PythonTranslationProcess(PythonReorganizerRules.rules());
    }

    protected PythonBaseDetectionRule(
            final boolean isInventory,
            @Nonnull List<IDetectionRule<Tree>> detectionRules,
            @Nonnull List<IReorganizerRule> reorganizerRules) {
        this.isInventory = isInventory;
        this.detectionRules = detectionRules;
        this.pythonTranslationProcess = new PythonTranslationProcess(reorganizerRules);
    }

    @Override
    public void visitCallExpression(@Nonnull CallExpression tree) {
        if (CryptoTrace.isEnabled()) {
            String file = new PythonScanContext(this.getContext()).getFilePath();
            int line = tree.firstToken().line();
            String callee =
                    Optional.ofNullable(tree.calleeSymbol())
                            .map(Symbol::name)
                            .orElse("<unknown>");
            if (LOG.isTraceEnabled()) {
                LOG.trace(
                        CryptoTrace.fmt(
                                this,
                                "visitCallExpression",
                                "file=" + file + ":" + line + " callee=" + callee));
                LOG.trace(
                        CryptoTrace.fmt(
                                this,
                                "visitCallExpression",
                                "running " + detectionRules.size() + " detection rules"));
            } else if (LOG.isDebugEnabled()) {
                LOG.debug(
                        CryptoTrace.fmt(
                                this,
                                "visitCallExpression",
                                "file=" + file + ":" + line + " callee=" + callee));
                LOG.debug(
                        CryptoTrace.fmt(
                                this,
                                "visitCallExpression",
                                "running " + detectionRules.size() + " detection rules"));
            }
        }
        detectionRules.forEach(
                rule -> {
                    if (CryptoTrace.isEnabled()) {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace(
                                    CryptoTrace.fmt(
                                            this,
                                            "visitCallExpression",
                                            "rule=" + rule.bundle().getIdentifier()));
                        } else if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                    CryptoTrace.fmt(
                                            this,
                                            "visitCallExpression",
                                            "rule=" + rule.bundle().getIdentifier()));
                        }
                    }
                    DetectionExecutive<PythonCheck, Tree, Symbol, PythonVisitorContext>
                            detectionExecutive =
                                    PythonAggregator.getLanguageSupport()
                                            .createDetectionExecutive(
                                                    tree,
                                                    rule,
                                                    new PythonScanContext(this.getContext()));
                    detectionExecutive.subscribe(this);
                    detectionExecutive.start();
                });
        super.visitCallExpression(tree); // Necessary to visit children nodes of this CallExpression
    }

    /**
     * Updates the output file with the translated nodes resulting from a finding.
     *
     * @param finding A finding containing detection store information.
     */
    @Override
    public void update(@Nonnull Finding<PythonCheck, Tree, Symbol, PythonVisitorContext> finding) {
        List<INode> nodes = pythonTranslationProcess.initiate(finding.detectionStore());
        if (CryptoTrace.isEnabled()) {
            if (LOG.isTraceEnabled()) {
                nodes.forEach(
                        n ->
                                LOG.trace(
                                        CryptoTrace.fmt(
                                                this,
                                                "update",
                                                "asset="
                                                        + n.getKind().getSimpleName())));
            } else if (LOG.isDebugEnabled()) {
                nodes.forEach(
                        n ->
                                LOG.debug(
                                        CryptoTrace.fmt(
                                                this,
                                                "update",
                                                "asset="
                                                        + n.getKind().getSimpleName())));
            }
        }
        if (isInventory) {
            PythonAggregator.addNodes(nodes);
        }
        // report
        this.report(finding.getMarkerTree(), nodes)
                .forEach(
                        issue ->
                                finding.detectionStore()
                                        .getScanContext()
                                        .reportIssue(this, issue.tree(), issue.message()));
    }

    @Override
    @Nonnull
    public List<Issue<Tree>> report(
            @Nonnull Tree markerTree, @Nonnull List<INode> translatedNodes) {
        // override by higher level rule, to report an issue
        return Collections.emptyList();
    }
}
