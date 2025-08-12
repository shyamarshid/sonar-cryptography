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

public final class CxxTranslationProcess
        extends ITranslationProcess<Object, Object, Object, Object> {

    public CxxTranslationProcess() {
        super(List.of());
    }

    @Nonnull
    @Override
    public List<INode> initiate(
            @Nonnull DetectionStore<Object, Object, Object, Object> rootDetectionStore) {
        CxxTranslator translator = new CxxTranslator();
        List<INode> translated = translator.translate(rootDetectionStore);
        Reorganizer reorganizer = new Reorganizer(reorganizerRules);
        List<INode> reorganized = reorganizer.reorganize(translated);
        return Enricher.enrich(reorganized).stream().toList();
    }
}

