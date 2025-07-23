package com.ibm.plugin.rules;

import com.ibm.plugin.rules.detection.CBaseDetectionRule;
import com.ibm.rules.InventoryRule;
import com.ibm.rules.issue.Issue;
import com.ibm.mapper.model.INode;
import java.util.List;
import javax.annotation.Nonnull;
import org.sonar.check.Rule;

@Rule(key = "Inventory")
public class CInventoryRule extends CBaseDetectionRule {
    @Override
    public @Nonnull List<Issue<Object>> report(
            @Nonnull Object markerTree, @Nonnull List<INode> nodes) {
        return new InventoryRule<Object>().report(markerTree, nodes);
    }
}
