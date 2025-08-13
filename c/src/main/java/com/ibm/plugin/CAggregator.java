package com.ibm.plugin;

import com.ibm.engine.language.ILanguageSupport;
import com.ibm.engine.language.LanguageSupporter;
import com.ibm.mapper.model.INode;
import com.ibm.output.IAggregator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public final class CAggregator implements IAggregator {
    private static final Logger LOG = Loggers.get(CAggregator.class);
    private static final String ORIGIN = CAggregator.class.getSimpleName() + ".java";

    private static ILanguageSupport<Object, Object, Object, Object> cLanguageSupport =
            LanguageSupporter.cLanguageSupporter();
    private static List<INode> detectedNodes = new ArrayList<>();

    private CAggregator() {}

    public static void addNodes(@Nonnull List<INode> newNodes) {
        detectedNodes.addAll(newNodes);
        IAggregator.log(newNodes);
        LOG.info(
                "CXX {}: event=<aggregate> added={} total={}",
                ORIGIN,
                newNodes.size(),
                detectedNodes.size());
    }

    @Nonnull
    public static List<INode> getDetectedNodes() {
        return Collections.unmodifiableList(detectedNodes);
    }

    @Nonnull
    public static ILanguageSupport<Object, Object, Object, Object> getLanguageSupport() {
        return cLanguageSupport;
    }

    public static void reset() {
        cLanguageSupport = LanguageSupporter.cLanguageSupporter();
        detectedNodes = new ArrayList<>();
    }
}
