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
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CxxLanguageSupport implements ILanguageSupport<Object, Object, Object, Object> {
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
        return CxxBaseMethodVisitor::new;
    }

    @Nonnull
    @Override
    public Optional<Object> getEnclosingMethod(@Nonnull Object expression) {
        return Optional.empty();
    }

    @Nullable
    @Override
    public MethodMatcher<Object> createMethodMatcherBasedOn(@Nonnull Object methodDefinition) {
        return null;
    }

    @Nullable
    @Override
    public EnumMatcher<Object> createSimpleEnumMatcherFor(
            @Nonnull Object enumIdentifier, @Nonnull MatchContext matchContext) {
        return null;
    }
}
