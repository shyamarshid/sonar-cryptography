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
package com.ibm.engine.language.python;

import com.ibm.engine.detection.IType;
import com.ibm.engine.detection.MatchContext;
import com.ibm.engine.language.ILanguageTranslation;
import com.ibm.util.CryptoTrace;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.tree.Argument;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.Name;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.RegularArgument;
import org.sonar.plugins.python.api.tree.Tree;

public class PythonLanguageTranslation implements ILanguageTranslation<Tree> {

    private static final Logger LOG = Loggers.get(PythonLanguageTranslation.class);

    @Nonnull
    @Override
    public Optional<String> getMethodName(
            @Nonnull MatchContext matchContext, @Nonnull Tree methodInvocation) {
        Optional<String> res = Optional.empty();
        if (methodInvocation instanceof CallExpression callExpression) {
            Symbol methodInvocationSymbol = callExpression.calleeSymbol();
            if (methodInvocationSymbol != null) {
                res = Optional.of(methodInvocationSymbol.name());
            } else if (callExpression.callee() instanceof Name nameTree) {
                res = Optional.of(nameTree.name());
            }
        }
        if (LOG.isTraceEnabled() && CryptoTrace.isEnabled()) {
            LOG.trace(
                    CryptoTrace.fmt(
                            this,
                            "getMethodName",
                            "name=" + res.orElse("<empty>")));
        }
        return res;
    }

    @Nonnull
    @Override
    public Optional<IType> getInvokedObjectTypeString(
            @Nonnull MatchContext matchContext, @Nonnull Tree methodInvocation) {
        // This method should return the type of the *invoked object* (or qualifier): for a method
        // invocation `X25519PrivateKey.generate()`, it should return
        // `cryptography.hazmat.primitives.asymmetric.X25519PrivateKey`, and for
        // `global_var.bit_count()`, it should return the type of `global_var`.
        Optional<IType> res = Optional.empty();
        if (methodInvocation instanceof CallExpression callExpression) {
            if (callExpression.callee() instanceof QualifiedExpression qualifiedExpression) {
                res = PythonSemantic.resolveTreeType(qualifiedExpression.name());
            } else if (callExpression.callee() instanceof Name functionName) {
                res = PythonSemantic.resolveTreeType(functionName);
            }
        }
        if (LOG.isTraceEnabled() && CryptoTrace.isEnabled()) {
            LOG.trace(
                    CryptoTrace.fmt(
                            this,
                            "getInvokedObjectTypeString",
                            "type=" + res.map(Object::toString).orElse("<empty>")));
        }
        return res;
    }

    @Override
    public @Nonnull Optional<IType> getMethodReturnTypeString(
            @Nonnull MatchContext matchContext, @Nonnull Tree methodInvocation) {
        // TODO: This does not take the subscriptionIndex into account, so it will return an IType
        // accepting the type of all
        Optional<IType> res = PythonSemantic.resolveTreeType(methodInvocation);
        if (LOG.isTraceEnabled() && CryptoTrace.isEnabled()) {
            LOG.trace(
                    CryptoTrace.fmt(
                            this,
                            "getMethodReturnTypeString",
                            "type=" + res.map(Object::toString).orElse("<empty>")));
        }
        return res;
    }

    @Override
    public @Nonnull List<IType> getMethodParameterTypes(
            @Nonnull MatchContext matchContext, @Nonnull Tree methodInvocation) {
        List<IType> res = Collections.emptyList();
        if (methodInvocation instanceof CallExpression callExpression) {
            List<Argument> arguments = callExpression.arguments();
            if (!arguments.isEmpty()) {
                res =
                        arguments.stream()
                                .filter(RegularArgument.class::isInstance)
                                .map(
                                        argument ->
                                                PythonSemantic.resolveTreeType(
                                                        ((RegularArgument) argument).expression()))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .toList();
            }
        }
        if (LOG.isTraceEnabled() && CryptoTrace.isEnabled()) {
            String types = res.stream().map(Object::toString).collect(Collectors.joining(","));
            LOG.trace(
                    CryptoTrace.fmt(
                            this,
                            "getMethodParameterTypes",
                            "types=" + types));
        }
        return res;
    }

    @Override
    public @Nonnull Optional<String> resolveIdentifierAsString(
            @Nonnull MatchContext matchContext, @Nonnull Tree name) {
        Optional<String> res = Optional.empty();
        if (name instanceof Name nameTree) {
            res = Optional.of(nameTree.name());
        }
        if (LOG.isTraceEnabled() && CryptoTrace.isEnabled()) {
            LOG.trace(
                    CryptoTrace.fmt(
                            this,
                            "resolveIdentifierAsString",
                            "id=" + res.orElse("<empty>")));
        }
        return res;
    }

    @Override
    public @Nonnull Optional<String> getEnumIdentifierName(
            @Nonnull MatchContext matchContext, @Nonnull Tree enumIdentifier) {
        // TODO: Implement enums in the Python case?
        return Optional.empty();
    }

    @Override
    public @Nonnull Optional<String> getEnumClassName(
            @Nonnull MatchContext matchContext, @Nonnull Tree enumClass) {
        // TODO: Implement enums in the Python case?
        return Optional.empty();
    }
}
