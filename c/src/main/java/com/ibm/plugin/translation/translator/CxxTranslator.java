package com.ibm.plugin.translation.translator;

import com.ibm.engine.model.IValue;
import com.ibm.engine.model.ValueAction;
import com.ibm.engine.model.context.DigestContext;
import com.ibm.engine.model.context.IDetectionContext;
import com.ibm.engine.rule.IBundle;
import com.ibm.mapper.ITranslator;
import com.ibm.mapper.model.Algorithm;
import com.ibm.mapper.model.Cipher;
import com.ibm.mapper.model.MessageDigest;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.utils.DetectionLocation;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.sonar.cxx.sslr.api.AstNode;
import com.sonar.cxx.sslr.api.Token;

public final class CxxTranslator extends ITranslator<Object, Object, Object, Object> {

    @Nonnull
    @Override
    protected Optional<INode> translate(
            @Nonnull IBundle bundleIdentifier,
            @Nonnull IValue<Object> value,
            @Nonnull IDetectionContext detectionValueContext,
            @Nonnull String filePath) {
        if (value instanceof ValueAction<?> action) {
            DetectionLocation location =
                    getDetectionContextFrom(action.getLocation(), bundleIdentifier, filePath);
            if (location == null) {
                return Optional.empty();
            }
            Class<? extends com.ibm.mapper.model.IPrimitive> kind = Cipher.class;
            if (detectionValueContext.is(DigestContext.class)) {
                kind = MessageDigest.class;
            }
            Algorithm algo = new Algorithm(action.asString(), kind, location);
            return Optional.of(algo);
        }
        return Optional.empty();
    }

    @Override
    protected @Nullable DetectionLocation getDetectionContextFrom(
            @Nonnull Object location, @Nonnull IBundle bundle, @Nonnull String filePath) {
        if (location instanceof AstNode node) {
            Token token = node.getToken();
            int line = token.getLine();
            int column = token.getColumn();
            String keyword = token.getValue();
            return new DetectionLocation(filePath, line, column, List.of(keyword), bundle);
        }
        return null;
    }
}

