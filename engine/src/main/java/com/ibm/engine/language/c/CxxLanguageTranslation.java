package com.ibm.engine.language.c;

import com.ibm.engine.detection.IType;
import com.ibm.engine.detection.MatchContext;
import com.ibm.engine.language.ILanguageTranslation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public class CxxLanguageTranslation implements ILanguageTranslation<Object> {
    @Nonnull
    @Override
    public Optional<String> getMethodName(@Nonnull MatchContext matchContext, @Nonnull Object methodInvocation) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<IType> getInvokedObjectTypeString(@Nonnull MatchContext matchContext, @Nonnull Object methodInvocation) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<IType> getMethodReturnTypeString(@Nonnull MatchContext matchContext, @Nonnull Object methodInvocation) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public List<IType> getMethodParameterTypes(@Nonnull MatchContext matchContext, @Nonnull Object methodInvocation) {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public Optional<String> resolveIdentifierAsString(@Nonnull MatchContext matchContext, @Nonnull Object name) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<String> getEnumIdentifierName(@Nonnull MatchContext matchContext, @Nonnull Object enumIdentifier) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<String> getEnumClassName(@Nonnull MatchContext matchContext, @Nonnull Object enumClass) {
        return Optional.empty();
    }
}
