package com.ibm.plugin.translation;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.enricher.Enricher;
import com.ibm.mapper.ITranslationProcess;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.reorganizer.Reorganizer;
import com.ibm.mapper.reorganizer.IReorganizerRule;
import com.ibm.plugin.translation.translator.CxxTranslator;
import java.util.List;
import javax.annotation.Nonnull;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public final class CxxTranslationProcess
        extends ITranslationProcess<Object, Object, Object, Object> {
    private static final Logger LOG = Loggers.get(CxxTranslationProcess.class);
    private static final String ORIGIN = CxxTranslationProcess.class.getSimpleName() + ".java";

    public CxxTranslationProcess() {
        super(List.of());
    }

    @Nonnull
    @Override
    public List<INode> initiate(
            @Nonnull DetectionStore<Object, Object, Object, Object> rootDetectionStore) {
        LOG.info(
                "CXX {}: event=<translate-start> findingId={}",
                ORIGIN,
                rootDetectionStore.getStoreId());
        CxxTranslator translator = new CxxTranslator();
        List<INode> translated = translator.translate(rootDetectionStore);
        Reorganizer reorganizer = new Reorganizer(reorganizerRules);
        List<INode> reorganized = reorganizer.reorganize(translated);
        List<INode> result = Enricher.enrich(reorganized).stream().toList();
        LOG.info(
                "CXX {}: event=<translate-end> findingId={} nodes={}",
                ORIGIN,
                rootDetectionStore.getStoreId(),
                result.size());
        return result;
    }
}

