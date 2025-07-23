package com.ibm.engine.language.c;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.engine.detection.Handler;
import com.ibm.engine.detection.IDetectionEngine;
import com.ibm.engine.detection.IType;
import com.ibm.engine.detection.MatchContext;
import com.ibm.engine.detection.MethodMatcher;
import com.ibm.engine.rule.Parameter;
import com.ibm.engine.detection.ResolvedValue;
import com.ibm.engine.detection.TraceSymbol;
import com.ibm.engine.executive.DetectionExecutive;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CxxDetectionEngine implements IDetectionEngine<Object, Object> {
    private final DetectionStore<Object, Object, Object, Object> detectionStore;
    private final Handler<Object, Object, Object, Object> handler;

    public CxxDetectionEngine(
            @Nonnull DetectionStore<Object, Object, Object, Object> detectionStore,
            @Nonnull Handler<Object, Object, Object, Object> handler) {
        this.detectionStore = detectionStore;
        this.handler = handler;
    }

    @Override
    public void run(@Nonnull Object tree) {}

    @Override
    public void run(@Nonnull TraceSymbol<Object> traceSymbol, @Nonnull Object tree) {}

    @Override
    public @Nullable Object extractArgumentFromMethodCaller(
            @Nonnull Object methodDefinition,
            @Nonnull Object methodInvocation,
            @Nonnull Object methodParameterIdentifier) {
        return null;
    }

    @Nonnull
    @Override
    public <O> List<ResolvedValue<O, Object>> resolveValuesInInnerScope(
            @Nonnull Class<O> clazz,
            @Nonnull Object expression,
            @Nullable com.ibm.engine.model.factory.IValueFactory<Object> valueFactory) {
        return Collections.emptyList();
    }

    @Override
    public void resolveValuesInOuterScope(@Nonnull Object expression, @Nonnull Parameter<Object> parameter) {}

    @Override
    public <O> void resolveMethodReturnValues(
            @Nonnull Class<O> clazz, @Nonnull Object methodDefinition, @Nonnull Parameter<Object> parameter) {}

    @Nullable
    @Override
    public <O> ResolvedValue<O, Object> resolveEnumValue(
            @Nonnull Class<O> clazz,
            @Nonnull Object enumClassDefinition,
            @Nonnull LinkedList<Object> selections) {
        return null;
    }

    @Nonnull
    @Override
    public Optional<TraceSymbol<Object>> getAssignedSymbol(@Nonnull Object expression) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<TraceSymbol<Object>> getMethodInvocationParameterSymbol(
            @Nonnull Object methodInvocation, @Nonnull Parameter<Object> parameter) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<TraceSymbol<Object>> getNewClassParameterSymbol(
            @Nonnull Object newClass, @Nonnull Parameter<Object> parameter) {
        return Optional.empty();
    }

    @Override
    public boolean isInvocationOnVariable(Object methodInvocation, @Nonnull TraceSymbol<Object> variableSymbol) {
        return false;
    }

    @Override
    public boolean isInitForVariable(Object newClass, @Nonnull TraceSymbol<Object> variableSymbol) {
        return false;
    }
}
