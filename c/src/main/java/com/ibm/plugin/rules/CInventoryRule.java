package com.ibm.plugin.rules;

import com.ibm.engine.detection.Finding;
import com.ibm.engine.executive.DetectionExecutive;
import com.ibm.engine.language.c.CxxScanContext;
import com.ibm.mapper.model.INode;
import com.ibm.plugin.CAggregator;
import com.ibm.plugin.rules.detection.wolfcrypt.WolfCryptRules;
import com.ibm.plugin.translation.CxxTranslationProcess;
import com.ibm.rules.InventoryRule;
import com.ibm.rules.IReportableDetectionRule;
import com.ibm.rules.issue.Issue;
import com.sonar.cxx.sslr.api.AstNode;
import com.sonar.cxx.sslr.api.GenericTokenType;
import java.util.List;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.check.Rule;
import org.sonar.cxx.parser.CxxGrammarImpl;
import org.sonar.cxx.parser.CxxPunctuator;
import org.sonar.cxx.squidbridge.checks.SquidCheck;

@Rule(key = "Inventory", name = "Cryptographic Inventory (CBOM)")
public class CInventoryRule extends SquidCheck<com.sonar.cxx.sslr.api.Grammar>
        implements IReportableDetectionRule<Object>,
                com.ibm.common.IObserver<Finding<Object, Object, Object, Object>> {

    private static final Logger LOG = LoggerFactory.getLogger(CInventoryRule.class);

    @Nonnull private final CxxTranslationProcess translationProcess = new CxxTranslationProcess();

    @Override
    public void init() {
        subscribeTo(CxxGrammarImpl.postfixExpression);
    }

    @Override
    public void visitNode(AstNode node) {
        if (node.getFirstChild(CxxPunctuator.BR_LEFT) == null) {
            return;
        }

        AstNode idNode = node.getLastChild(GenericTokenType.IDENTIFIER);
        if (idNode == null) {
            return;
        }
        String callee = idNode.getTokenValue();

        if (LOG.isTraceEnabled()) {
            LOG.trace(
                    "C CALL {} @ {}:{}",
                    callee,
                    getContext().getInputFile().filename(),
                    idNode.getToken().getLine());
        }

        WolfCryptRules.rules()
                .forEach(
                        rule -> {
                            DetectionExecutive<Object, Object, Object, Object> exec =
                                    CAggregator.getLanguageSupport()
                                            .createDetectionExecutive(
                                                    node, rule, new CxxScanContext(getContext().getInputFile()));
                            exec.subscribe(this);
                            exec.start();
                        });
        super.visitNode(node);
    }

    @Override
    public void update(@Nonnull Finding<Object, Object, Object, Object> finding) {
        List<INode> nodes = translationProcess.initiate(finding.detectionStore());
        CAggregator.addNodes(nodes);
        this.report(finding.getMarkerTree(), nodes)
                .forEach(
                        issue ->
                                finding.detectionStore()
                                        .getScanContext()
                                        .reportIssue(this, issue.tree(), issue.message()));
    }

    @Override
    public @Nonnull List<Issue<Object>> report(
            @Nonnull Object markerTree, @Nonnull List<INode> nodes) {
        return new InventoryRule<Object>().report(markerTree, nodes);
    }
}

