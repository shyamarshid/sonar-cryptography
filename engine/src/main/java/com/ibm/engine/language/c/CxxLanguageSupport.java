package com.ibm.engine.language.c;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.engine.detection.EnumMatcher;
import com.ibm.engine.detection.Handler;
import com.ibm.engine.detection.IBaseMethodVisitorFactory;
import com.ibm.engine.detection.IDetectionEngine;
import com.ibm.engine.detection.MatchContext;
import com.ibm.engine.detection.MethodMatcher;
import com.ibm.engine.executive.DetectionExecutive;
import com.ibm.engine.language.ILanguageSupport;
import com.ibm.engine.language.ILanguageTranslation;
import com.ibm.engine.language.IScanContext;
import com.ibm.engine.rule.IDetectionRule;
import com.sonar.cxx.sslr.api.AstNode;
import com.sonar.cxx.sslr.api.GenericTokenType;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class CxxLanguageSupport implements ILanguageSupport<Object, Object, Object, Object> {
    private static final Logger LOG = Loggers.get(CxxLanguageSupport.class);
    private static final String ORIGIN = CxxLanguageSupport.class.getSimpleName() + ".java";

    @Nonnull private final Handler<Object, Object, Object, Object> handler;

    public CxxLanguageSupport() {
        this.handler = new Handler<>(this);
    }

    @Nonnull
    @Override
    public ILanguageTranslation<Object> translation() {
        return new CxxLanguageTranslation();
    }

    @Nonnull
    @Override
    public DetectionExecutive<Object, Object, Object, Object> createDetectionExecutive(
            @Nonnull Object tree,
            @Nonnull IDetectionRule<Object> detectionRule,
            @Nonnull IScanContext<Object, Object> scanContext) {
        return new DetectionExecutive<>(tree, detectionRule, scanContext, this.handler);
    }

    @Nonnull
    @Override
    public IDetectionEngine<Object, Object> createDetectionEngineInstance(
            @Nonnull DetectionStore<Object, Object, Object, Object> detectionStore) {
        return new CxxDetectionEngine(detectionStore, this.handler);
    }

    @Nonnull
    @Override
    public IBaseMethodVisitorFactory<Object, Object> getBaseMethodVisitorFactory() {
        return (traceSymbol, detectionEngine) ->
                new CxxBaseMethodVisitor(traceSymbol, detectionEngine);
    }

    @Nonnull
    @Override
    public Optional<Object> getEnclosingMethod(@Nonnull Object expression) {
        return Optional.empty();
    }

    @Nullable
    @Override
    public MethodMatcher<Object> createMethodMatcherBasedOn(@Nonnull Object methodDefinition) {
        if (methodDefinition instanceof AstNode node) {
            AstNode idNode = node.getLastChild(GenericTokenType.IDENTIFIER);
            if (idNode != null) {
                String name = idNode.getTokenValue();
                String filePath = "<n/a>";
                String line = idNode.getToken() != null ? String.valueOf(idNode.getToken().getLine()) : "<n/a>";
                LOG.info(
                        "CXX {}: event=<matcher> src={} callee={}",
                        ORIGIN,
                        filePath + ":" + line,
                        name);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("CXX matcher built for callee '{}'", name);
                }
                return new MethodMatcher<>(
                        new String[] {MethodMatcher.ANY}, new String[] {name});
            }
        }
        return null;
    }

    @Nullable
    @Override
    public EnumMatcher<Object> createSimpleEnumMatcherFor(
            @Nonnull Object enumIdentifier, @Nonnull MatchContext matchContext) {
        return null;
    }
}
