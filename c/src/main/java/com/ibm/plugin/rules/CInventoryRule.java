package com.ibm.plugin.rules;

import com.ibm.engine.detection.Finding;
import com.ibm.engine.executive.DetectionExecutive;
import com.ibm.engine.language.c.CxxScanContext;
import com.ibm.engine.rule.IDetectionRule;
import com.ibm.mapper.model.INode;
import com.ibm.plugin.CAggregator;
import com.ibm.plugin.rules.detection.wolfcrypt.WolfCryptRules;
import com.ibm.plugin.translation.CxxTranslationProcess;
import com.ibm.rules.IReportableDetectionRule;
import com.ibm.rules.InventoryRule;
import com.ibm.rules.issue.Issue;
import com.sonar.cxx.sslr.api.AstNode;
import com.sonar.cxx.sslr.api.GenericTokenType;
import com.sonar.cxx.sslr.api.Grammar;
import java.util.List;
import javax.annotation.Nonnull;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.check.Rule;
import org.sonar.cxx.parser.CxxGrammarImpl;
import org.sonar.cxx.parser.CxxPunctuator;
import org.sonar.cxx.squidbridge.checks.SquidCheck;
import org.sonar.cxx.squidbridge.annotations.ActivatedByDefault;

@Rule(key = "Inventory", name = "Cryptographic Inventory (CBOM)")
@ActivatedByDefault
public class CInventoryRule extends SquidCheck<Grammar>
    implements IReportableDetectionRule<Object>,
        com.ibm.common.IObserver<Finding<Object, Object, Object, Object>> {

  private static final Logger LOG = Loggers.get(CInventoryRule.class);
  private static final String ORIGIN = CInventoryRule.class.getSimpleName() + ".java";

  @Nonnull
  private final CxxTranslationProcess translationProcess = new CxxTranslationProcess();

  @Override
  public void init() {
    LOG.info("CXX probe: CInventoryRule#inside_init");
    // Subscribe to function-call candidates; filter by '(' below
    subscribeTo(CxxGrammarImpl.postfixExpression);
    LOG.info(
        "CXX {}: event=<visitor-init> ruleKey={} nodeKinds=[postfixExpression]",
        ORIGIN,
        "Inventory");
  }

  @Override
  public void visitNode(AstNode node) {
    // Treat as call only if '(' is present in this postfix expression
    if (node.getFirstChild(CxxPunctuator.BR_LEFT) == null) {
      return;
    }

    AstNode idNode = node.getLastChild(GenericTokenType.IDENTIFIER);
    if (idNode == null) {
      return;
    }

    String callee = idNode.getTokenValue();
    String filePath =
        getContext().getInputFile() != null
            ? getContext().getInputFile().uri().getPath()
            : "<n/a>";
    String line =
        idNode.getToken() != null ? String.valueOf(idNode.getToken().getLine()) : "<n/a>";

    LOG.info("CXX {}: event=<visit-node> src={} callee={}", ORIGIN, filePath + ":" + line, callee);

    List<IDetectionRule<Object>> rules = WolfCryptRules.rules();
    LOG.info(
        "CXX {}: event=<run-detection> src={} ruleCount={} callee={}",
        ORIGIN,
        filePath + ":" + line,
        rules.size(),
        callee);

    rules.forEach(
        rule -> {
          DetectionExecutive<Object, Object, Object, Object> exec =
              CAggregator.getLanguageSupport()
                  .createDetectionExecutive(
                      node, rule, new CxxScanContext(getContext().getInputFile()));
          exec.subscribe(this);
          exec.start();
        });
  }

  @Override
  public void update(@Nonnull Finding<Object, Object, Object, Object> finding) {
    String filePath = finding.detectionStore().getScanContext().getFilePath();
    String line = "<n/a>";
    Object marker = finding.getMarkerTree();
    if (marker instanceof AstNode ast) {
      line = String.valueOf(ast.getToken().getLine());
    }

    String asset =
        finding
            .detectionStore()
            .getDetectionRule()
            .detectionValueContext()
            .type()
            .getSimpleName();

    String alg =
        finding.detectionStore().getActionValue().map(v -> v.asString()).orElse("<n/a>");

    LOG.info(
        "CXX {}: event=<finding> src={} asset={} alg={}",
        ORIGIN,
        filePath + ":" + line,
        asset,
        alg);

    List<INode> nodes = translationProcess.initiate(finding.detectionStore());
    CAggregator.addNodes(nodes);

    this.report(finding.getMarkerTree(), nodes)
        .forEach(
            issue ->
                finding
                    .detectionStore()
                    .getScanContext()
                    .reportIssue(this, issue.tree(), issue.message()));
  }

  @Override
  public @Nonnull List<Issue<Object>> report(
      @Nonnull Object markerTree, @Nonnull List<INode> nodes) {
    return new InventoryRule<Object>().report(markerTree, nodes);
  }
}
