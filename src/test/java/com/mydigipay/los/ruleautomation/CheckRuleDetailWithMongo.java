package com.mydigipay.los.ruleautomation;

import com.mydigipay.los.ruleautomation.model.group.Group;
import com.mydigipay.los.ruleautomation.model.profile.OnboardingProfile;
import com.mydigipay.los.ruleautomation.model.rule.Rule;
import com.mydigipay.los.ruleautomation.repository.CreditProfileRepository;
import com.mydigipay.los.ruleautomation.repository.GroupRepository;
import com.mydigipay.los.ruleautomation.repository.RuleRepository;
import com.mydigipay.los.ruleautomation.service.ExcelController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.util.Optional;

@SpringBootTest
@ContextConfiguration
/*
 * Author: b.arabi
 */
public class CheckRuleDetailWithMongo extends AbstractTestNGSpringContextTests {

    @Autowired
    RuleRepository ruleRepository;
    @Autowired
    ExcelController excelController;
    @Autowired
    GroupRepository groupRepo;
    @Autowired
    CreditProfileRepository profileRepo;
    private SoftAssert softAssert;

    private String excelAddress;

    @Parameters("ruleDetailFile")
    @BeforeClass
    public void setup(String ruleDetailFile) {
        softAssert = new SoftAssert();
        this.excelAddress = ruleDetailFile;

    }

    @DataProvider(name = "Read rules from excel file")
    public Rule[] findRules() {

        Rule[] rules;
        try {
            rules = ExcelController.excelReader(excelAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rules;
    }

    @Test(dataProvider = "Read rules from excel file")
    void CheckRuleParams(Rule excelRule) {
        Group group = groupRepo.findByRuleId(excelRule.getRuleId());
        Rule rule = ruleRepository.findByRuleId(excelRule.getRuleId());
        OnboardingProfile profile= profileRepo.findByProfileId(excelRule.getProfileId());

        // Customer type checking
        softAssert.assertEquals(rule.getCustomerType(), excelRule.getCustomerType(), "For rule id:" + excelRule.getRuleId() + " customer type mismatch.");

        // Activation offset checking
        softAssert.assertEquals(rule.getActivationOffset(), excelRule.getActivationOffset(), "For rule id:" + excelRule.getRuleId() + " activation offset mismatch.");

        // Expiration offset from activation checking
        softAssert.assertEquals(rule.getExpirationOffsetFromActivation(), excelRule.getExpirationOffsetFromActivation(), "For rule id:" + excelRule.getRuleId() + " expiration offset from activation mismatch.");

        // Expiration offset checking
        softAssert.assertEquals(rule.getExpirationOffset(), excelRule.getExpirationOffset(), "For rule id:" + excelRule.getRuleId() + " expiration offset mismatch.");

        // Register offset checking
        softAssert.assertEquals(rule.getRegisterOffset(), excelRule.getRegisterOffset(), "For rule id:" + excelRule.getRuleId() + " register offset mismatch.");

        // Type checking
        softAssert.assertEquals(rule.getType(), excelRule.getType(), "For rule id:" + excelRule.getRuleId() + " type mismatch.");

        // Installment payable checking
        softAssert.assertEquals(rule.getInstallmentPayable(), excelRule.getInstallmentPayable(), "For rule id:" + excelRule.getRuleId() + " installment payable mismatch.");

        // Installment offset checking
        softAssert.assertEquals(rule.getInstallmentOffset(), excelRule.getInstallmentOffset(), "For rule id:" + excelRule.getRuleId() + " installment offset mismatch.");

        // Interest percentage checking
        softAssert.assertEquals(rule.getInterestPercentage(), excelRule.getInterestPercentage(), "For rule id:" + excelRule.getRuleId() + " interest percentage mismatch.");

        // Prepayment percentage checking
        softAssert.assertEquals(rule.getPrepaymentPercentage(), excelRule.getPrepaymentPercentage(), "For rule id:" + excelRule.getRuleId() + " prepayment percentage mismatch.");

        // Fee charge percentage checking
        softAssert.assertEquals(rule.getFeeChargePercentage(), excelRule.getFeeChargePercentage(), "For rule id:" + excelRule.getRuleId() + " fee charge percentage mismatch.");

        // Bank acceptable score checking
        softAssert.assertEquals(rule.getBankAcceptableScore(), excelRule.getBankAcceptableScore(), "For rule id:" + excelRule.getRuleId() + " bank acceptable score mismatch.");

        // DP score result checking
        softAssert.assertEquals(rule.getDpScoreResult(), excelRule.getDpScoreResult(), "For rule id:" + excelRule.getRuleId() + " DP score result mismatch.");

        // Cheque relative support checking
        if (rule.getChequeRelativeSupport().size() != excelRule.getChequeRelativeSupport().size()) {
            softAssert.assertEquals(rule.getChequeRelativeSupport().size(), excelRule.getChequeRelativeSupport().size(), "For rule id:" + excelRule.getRuleId() + " cheque relative support mismatch.");
        } else {
            for (int i = 0; i < excelRule.getChequeRelativeSupport().size(); i++) {
                if (rule.getChequeRelativeSupport().contains(excelRule.getChequeRelativeSupport().get(i)))
                    softAssert.assertTrue(true, "mismatch in cheque relative support. relative support " + excelRule.getChequeRelativeSupport().get(i) + " hasn't been found.");
            }
        }

        // Close type checking
        softAssert.assertEquals(rule.getCloseType(), excelRule.getCloseType(), "For rule id:" + excelRule.getRuleId() + " close type mismatch.");

        // Is need to DP score checking
        softAssert.assertEquals(rule.getIsNeedToDpScore(), excelRule.getIsNeedToDpScore(), "For rule id:" + excelRule.getRuleId() + " is need to dp score mismatch.");

        // Integrate score versioning matrix checking
        softAssert.assertEquals(rule.getIntegrateScoreVersioningMatrix(), excelRule.getIntegrateScoreVersioningMatrix(), "For rule id:" + excelRule.getRuleId() + " integrate score versioning matrix mismatch.");

        // DP integrate score versioning matrix checking
        softAssert.assertEquals(rule.getDpIntegrateScoreVersioningMatrix(), excelRule.getDpIntegrateScoreVersioningMatrix(), "For rule id:" + excelRule.getRuleId() + " dp integrate score versioning matrix mismatch.");

        // Supported collateral types checking
        if (rule.getSupportedCollateralTypes().size() != excelRule.getSupportedCollateralTypes().size()) {
            softAssert.assertEquals(rule.getSupportedCollateralTypes().size(), excelRule.getSupportedCollateralTypes().size(), "For rule id:" + excelRule.getRuleId() + " supported collateral types mismatch.");
        } else {
            for (int i = 0; i < excelRule.getSupportedCollateralTypes().size(); i++) {
                if (rule.getSupportedCollateralTypes().contains(excelRule.getSupportedCollateralTypes().get(i)))
                    softAssert.assertTrue(true, "mismatch in supported collateral type. collateral type " + excelRule.getSupportedCollateralTypes().get(i) + " hasn't been found.");
            }
        }
        // Fund provider checking
        softAssert.assertEquals(rule.getFundProvider().getFpCode(), excelRule.getFundProvider().getFpCode(), "For rule id:" + excelRule.getRuleId() + " Fund provider code mismatch.");
        softAssert.assertEquals(rule.getFundProvider().getName(), excelRule.getFundProvider().getName(), "For rule id:" + excelRule.getRuleId() + " Fund provider name mismatch.");

        // Balance checking
        softAssert.assertEquals(rule.getBalance().get("min"), excelRule.getBalance().get("min"), "For rule id:" + excelRule.getRuleId() + " minimum balance mismatch.");
        softAssert.assertEquals(rule.getBalance().get("max"), excelRule.getBalance().get("max"), "For rule id:" + excelRule.getRuleId() + " maximum balance mismatch.");
        // Check the service type
        softAssert.assertEquals(Optional.ofNullable(rule.getServiceType()),profile.getServiceType().getValue());
        // Installment count checking
        softAssert.assertEquals(rule.getInstallmentCount().get("min"), excelRule.getInstallmentCount().get("min"), "For rule id:" + excelRule.getRuleId() + " minimum installment count mismatch.");
        softAssert.assertEquals(rule.getInstallmentCount().get("max"), excelRule.getInstallmentCount().get("max"), "For rule id:" + excelRule.getRuleId() + " maximum installment count mismatch.");

        // Steps checking
        if (rule.getSteps().size() != excelRule.getSteps().size()) {
            softAssert.assertEquals(rule.getSteps().size(), excelRule.getSteps().size(), "For rule id:" + excelRule.getRuleId() + " steps mismatch. There should be " + excelRule.getSteps().size() + " steps but actual is " + rule.getSteps().size() + " actual is: " + rule.getSteps() + " but expected is: " + excelRule.getSteps());
        } else {
            for (int i = 0; i < excelRule.getSteps().size(); i++) {
                softAssert.assertTrue(rule.getSteps().get(i).equals(excelRule.getSteps().get(i)), "For rule id:" + excelRule.getRuleId() + " mismatch in steps. expected is " + excelRule.getSteps().get(i) + " but actual is " + rule.getSteps().get(i));
            }
        }

        // Profile items checking
        if (rule.getProfileItems().size() != excelRule.getProfileItems().size()) {
            softAssert.assertEquals(rule.getProfileItems().size(), excelRule.getProfileItems().size(), "For rule id:" + excelRule.getRuleId() + " profile items mismatch. There should be " + excelRule.getProfileItems().size() + " items but there is " + rule.getProfileItems().size() + " actual is: " + rule.getProfileItems() + " but expected is: " + excelRule.getProfileItems());
        } else {
            for (int i = 0; i < excelRule.getProfileItems().size(); i++) {
                softAssert.assertTrue(rule.getProfileItems().get(i).equals(excelRule.getProfileItems().get(i)), "For rule id:" + excelRule.getRuleId() + " mismatch in profile items. Expected is " + excelRule.getProfileItems().get(i) + " but actual is " + rule.getProfileItems().get(i));
            }
        }

        softAssert.assertEquals(excelRule.getGroupTitle(), group.getTitle());
        softAssert.assertAll();
    }


}
