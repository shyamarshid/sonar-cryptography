package com.ibm.plugin.rules.detection.wolfcrypt;

import com.ibm.engine.model.context.CipherContext;
import com.ibm.engine.model.context.DigestContext;
import com.ibm.engine.model.factory.AlgorithmFactory;
import com.ibm.engine.rule.IDetectionRule;
import com.ibm.engine.rule.builder.DetectionRuleBuilder;
import java.util.List;
import javax.annotation.Nonnull;

public final class WolfCryptRules {
    private WolfCryptRules() {}

    private static final String LIB_TYPE = "wolfssl";

    private static final IDetectionRule<Object> SHA256_RULE =
            new DetectionRuleBuilder<Object>()
                    .createDetectionRule()
                    .forObjectTypes(LIB_TYPE)
                    .forMethods("wc_Sha256Final")
                    .shouldBeDetectedAs(new AlgorithmFactory<>("SHA-256"))
                    .withAnyParameters()
                    .buildForContext(new DigestContext())
                    .inBundle(() -> "WolfCrypt")
                    .withoutDependingDetectionRules();

    private static final IDetectionRule<Object> AES_RULE =
            new DetectionRuleBuilder<Object>()
                    .createDetectionRule()
                    .forObjectTypes(LIB_TYPE)
                    .forMethods("wc_AesCbcEncrypt")
                    .shouldBeDetectedAs(new AlgorithmFactory<>("AES"))
                    .withAnyParameters()
                    .buildForContext(new CipherContext())
                    .inBundle(() -> "WolfCrypt")
                    .withoutDependingDetectionRules();

    private static final IDetectionRule<Object> RSA_RULE =
            new DetectionRuleBuilder<Object>()
                    .createDetectionRule()
                    .forObjectTypes(LIB_TYPE)
                    .forMethods("wc_RsaPublicEncrypt")
                    .shouldBeDetectedAs(new AlgorithmFactory<>("RSA"))
                    .withAnyParameters()
                    .buildForContext(new CipherContext())
                    .inBundle(() -> "WolfCrypt")
                    .withoutDependingDetectionRules();

    @Nonnull
    public static List<IDetectionRule<Object>> rules() {
        return List.of(SHA256_RULE, AES_RULE, RSA_RULE);
    }
}
