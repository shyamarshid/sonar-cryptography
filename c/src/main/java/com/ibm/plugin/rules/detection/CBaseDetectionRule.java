package com.ibm.plugin.rules.detection;

import com.ibm.common.IObserver;
import com.ibm.engine.detection.Finding;
import com.ibm.rules.IReportableDetectionRule;
import com.ibm.rules.issue.Issue;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

public abstract class CBaseDetectionRule
        implements IObserver<Finding<Object, Object, Object, Object>>,
                IReportableDetectionRule<Object> {

    @Override
    public void update(@Nonnull Finding<Object, Object, Object, Object> finding) {
        // no-op
    }

    @Override
    public @Nonnull List<Issue<Object>> report(
            @Nonnull Object markerTree, @Nonnull List<com.ibm.mapper.model.INode> nodes) {
        return Collections.emptyList();
    }
}
