package com.ibm.engine.language.c;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.engine.detection.Handler;
import com.ibm.engine.detection.IDetectionEngine;
import com.ibm.engine.detection.ResolvedValue;
import com.ibm.engine.detection.TraceSymbol;
import com.ibm.engine.detection.MethodDetection;
import com.ibm.engine.rule.Parameter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import com.sonar.cxx.sslr.api.AstNode;
import com.sonar.cxx.sslr.api.GenericTokenType;
import org.sonar.cxx.parser.CxxPunctuator;

public class CxxDetectionEngine implements IDetectionEngine<Object, Object> {
    private static final Logger LOG = Loggers.get(CxxDetectionEngine.class);
    private static final String ORIGIN = CxxDetectionEngine.class.getSimpleName() + ".java";

    private final DetectionStore<Object, Object, Object, Object> detectionStore;
    private final Handler<Object, Object, Object, Object> handler;

    public CxxDetectionEngine(
            @Nonnull DetectionStore<Object, Object, Object, Object> detectionStore,
            @Nonnull Handler<Object, Object, Object, Object> handler) {
        this.detectionStore = detectionStore;
        this.handler = handler;
    }

    @Override
    public void run(@Nonnull Object tree) {
        run(TraceSymbol.createStart(), tree);
    }

    @Override
    public void run(@Nonnull TraceSymbol<Object> traceSymbol, @Nonnull Object tree) {
        AstNode node = tree instanceof AstNode ? (AstNode) tree : null;
        final String filePath = detectionStore.getScanContext().getFilePath();
        final String line =
                node != null && node.getToken() != null
                        ? String.valueOf(node.getToken().getLine())
                        : "<n/a>";
        final String nodeKind = node != null ? node.getType().toString() : "<n/a>";
        LOG.info(
                "CXX {}: event=<engine-run> src={} nodeKind={}",
                ORIGIN,
                filePath + ":" + line,
                nodeKind);
        if (node != null) {
            if (node.getFirstChild(CxxPunctuator.BR_LEFT) != null) {
                handler.addCallToCallStack(node, detectionStore.getScanContext());
                if (detectionStore
                        .getDetectionRule()
                        .match(node, handler.getLanguageSupport().translation())) {
                    AstNode idNode = node.getLastChild(GenericTokenType.IDENTIFIER);
                    String callee = idNode != null ? idNode.getTokenValue() : "<n/a>";
                    MethodDetection<Object> methodDetection = new MethodDetection<>(node, null);
                    detectionStore.onReceivingNewDetection(methodDetection);
                    detectionStore
                            .getActionValue()
                            .ifPresent(
                                    action -> {
                                        String asset =
                                                detectionStore
                                                        .getDetectionRule()
                                                        .detectionValueContext()
                                                        .type()
                                                        .getSimpleName();
                                        LOG.info(
                                                "CXX {}: event=<match> src={} rule={}/{} callee={} asset={} alg={}",
                                                ORIGIN,
                                                filePath + ":" + line,
                                                detectionStore
                                                        .getDetectionRule()
                                                        .bundle()
                                                        .getIdentifier(),
                                                detectionStore.getDetectionRule().getClass().getSimpleName(),
                                                callee,
                                                asset,
                                                action.asString());
                                        LOG.info(
                                                "CXX {}: event=<emit-finding> src={} id={} asset={}",
                                                ORIGIN,
                                                filePath + ":" + line,
                                                detectionStore.getStoreId(),
                                                asset);
                                    });
                }
            }
        }
    }

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
