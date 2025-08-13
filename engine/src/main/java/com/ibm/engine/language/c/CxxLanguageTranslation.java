package com.ibm.engine.language.c;

import com.ibm.engine.detection.IType;
import com.ibm.engine.detection.MatchContext;
import com.ibm.engine.language.ILanguageTranslation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import com.sonar.cxx.sslr.api.AstNode;
import com.sonar.cxx.sslr.api.GenericTokenType;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class CxxLanguageTranslation implements ILanguageTranslation<Object> {
    private static final Logger LOG = Loggers.get(CxxLanguageTranslation.class);
    private static final String ORIGIN = CxxLanguageTranslation.class.getSimpleName() + ".java";
    @Nonnull
    @Override
    public Optional<String> getMethodName(
            @Nonnull MatchContext matchContext, @Nonnull Object methodInvocation) {
        Optional<String> result = Optional.empty();
        if (methodInvocation instanceof AstNode node) {
            AstNode idNode = node.getLastChild(GenericTokenType.IDENTIFIER);
            if (idNode != null) {
                result = Optional.ofNullable(idNode.getTokenValue());
            }
        }
        LOG.info(
                "CXX {}: event=<get-method-name> name={}",
                ORIGIN,
                result.orElse(""));
        return result;
    }

    @Nonnull
    @Override
    public Optional<IType> getInvokedObjectTypeString(
            @Nonnull MatchContext matchContext, @Nonnull Object methodInvocation) {
        return Optional.of((IType) typeString -> "wolfssl".equals(typeString));
    }

    @Nonnull
    @Override
    public Optional<IType> getMethodReturnTypeString(@Nonnull MatchContext matchContext, @Nonnull Object methodInvocation) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public List<IType> getMethodParameterTypes(@Nonnull MatchContext matchContext, @Nonnull Object methodInvocation) {
        List<IType> result = Collections.emptyList();
        LOG.info(
                "CXX {}: event=<resolve> kind=param out={}",
                ORIGIN,
                result.size());
        return result;
    }

    @Nonnull
    @Override
    public Optional<String> resolveIdentifierAsString(
            @Nonnull MatchContext matchContext, @Nonnull Object name) {
        Optional<String> result = Optional.empty();
        if (name instanceof AstNode node) {
            result = Optional.ofNullable(node.getTokenValue());
        }
        LOG.info(
                "CXX {}: event=<resolve> kind=value out={}",
                ORIGIN,
                result.orElse(""));
        return result;
    }

    @Nonnull
    @Override
    public Optional<String> getEnumIdentifierName(@Nonnull MatchContext matchContext, @Nonnull Object enumIdentifier) {
        Optional<String> result = Optional.empty();
        LOG.info(
                "CXX {}: event=<resolve> kind=enum out={}",
                ORIGIN,
                "");
        return result;
    }

    @Nonnull
    @Override
    public Optional<String> getEnumClassName(@Nonnull MatchContext matchContext, @Nonnull Object enumClass) {
        Optional<String> result = Optional.empty();
        LOG.info(
                "CXX {}: event=<resolve> kind=enum out={}",
                ORIGIN,
                "");
        return result;
    }
}
