package com.mydigipay.los.ruleautomation;

import com.mydigipay.los.ruleautomation.exception.ImportException;
import com.mydigipay.los.ruleautomation.exception.RegisterException;
import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.model.address.Address;
import com.mydigipay.los.ruleautomation.model.rule.Rule;
import com.mydigipay.los.ruleautomation.model.steps.Step;
import com.mydigipay.los.ruleautomation.model.steps.cheque.pojo.ChequeDetailRequestModel;
import com.mydigipay.los.ruleautomation.model.steps.register.RegisterStep;
import com.mydigipay.los.ruleautomation.model.user.User;
import com.mydigipay.los.ruleautomation.model.wallet.CreditWallet;
import com.mydigipay.los.ruleautomation.repository.GroupRepository;
import com.mydigipay.los.ruleautomation.service.JsonController;
import com.mydigipay.los.ruleautomation.service.activationservice.ActivationService;
import com.mydigipay.los.ruleautomation.service.activationservice.FetchActivation;
import com.mydigipay.los.ruleautomation.service.dashboardservices.ArchiveActivation;
import com.mydigipay.los.ruleautomation.service.stepservices.StepService;
import com.mydigipay.los.ruleautomation.service.wallet.CreditWalletService;
import com.mydigipay.los.ruleautomation.utils.common.ConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootTest
@Slf4j
/*
 * Author: f.bahramnejad
 */
public class StepProcessTest extends AbstractTestNGSpringContextTests {


    final List<String> codes;
    final Rule rule;
    @Autowired
    StepService stepService;
    @Autowired
    RegisterStep registerStep;
    @Autowired
    GroupRepository grpRepo;
    @Autowired
    ConfigUtil configUtil;
    @Autowired
    ArchiveActivation cancelService;
    Map<String, Object> testData;
    @Autowired
    FetchActivation fetch;
    @Autowired
    ActivationService activationService;
    CreditWallet creditWallet;

    @Autowired
    CreditWalletService creditWalletService;
    private User testUser;
    private Activation activation;
    Boolean isAutoClose= false;


    public StepProcessTest(List<String> codes, Rule rule) {
        this.codes = codes;
        this.rule = rule;

    }


    @Parameters("testDataPath")
    @BeforeClass
    void setTestData(String testDataPath) throws IOException {
        /*
         * Reading test data file
         */
        int fpCode = rule.getFundProvider().getFpCode();
        testData = new HashMap<>();
        testUser = JsonController.jsonFileToPojoUsingKey(testDataPath, "user", User.class);
        Address address = JsonController.jsonFileToPojoUsingKey(testDataPath, "userAddress", Address.class);
        testData.put("Address", address);
        ChequeDetailRequestModel chequeDetailRequestModel = JsonController.propertyToPojo("chequeDetail" + fpCode, ChequeDetailRequestModel.class);
        testData.put("ChequeDetail", chequeDetailRequestModel);
        testData.put("AutoAllocation", rule.isAutomaticAllocation());
        testData.put("icsScore", configUtil.getProperty("scoreDetail.icsScore"));
        testData.put("bankScore", configUtil.getProperty("scoreDetail.bankScore"));
        testData.put("ContractNumber", configUtil.getProperty("contractNumber" + fpCode));
        testData.put("crn", rule.getCrn());
        creditWallet = new CreditWallet();
        if(rule.getCloseType().equals("AUTO")){
            isAutoClose=true;
        }


    }

    @DataProvider(name = "Find steps")
    public String[] findTestSteps() {

        return codes.toArray(new String[0]);

    }

    @Test
    void registerTest() throws ImportException, RegisterException, InterruptedException, IOException, ParseException {
        Reporter.log("Rule: " + rule.getRuleId());
        log.info("Register to rule: " + rule.getRuleId());
        registerStep.setRule(rule);
        registerStep.setTestData(testData);
        activation = registerStep.register(testUser);
        Assert.assertTrue(activation.checkStatusInJournal("REGISTERED"));
        activationService.setActivation(activation);
        Reporter.log("CreditId: " + activation.getCreditId());
        creditWallet.setBalance(activation.getInitialBalance());
        creditWallet.setCreditId(activation.getCreditId());
        creditWallet.setTitle(rule.getFundProvider().getName());
        creditWallet.setFundProviderCode(rule.getFundProvider().getFpCode());
        creditWallet.setServiceType(rule.getServiceType());

    }

    @Test(dataProvider = "Find steps", dependsOnMethods = {"registerTest"}, groups = {"stepTest"})
    void processSteps(String code) throws Exception {

        log.info(code + " Step process of rule " + rule.getRuleId());
        Step step = stepService.findStepByCode(code);
        step.setActivation(activation);
        step.setUser(testUser);
        step.setTestData(testData);
        step.process();
        Thread.sleep(5000);
        if (!step.getCompleteStatus().equals("ACTIVE")) {
            Assert.assertTrue(step.checkPostConditions(), "Status '" + step.getCompleteStatus() + "' doesn't exist in activation journal!\n");
        } else {
            Assert.assertTrue(step.checkPostConditions(), "Status is not 'ACTIVE' or there is a problem in contract summary!");
        }
        List<CreditWallet> creditWallets = creditWalletService.getListOfWallets(testUser);
        if (creditWallets != null) {
            Assert.assertTrue(creditWallets.stream().anyMatch(e -> e.equals(creditWallet)), "Current activation's wallet is not in credit wallets!");
        } else {
            Reporter.log("There is a problem in credit wallets API!");
        }

    }

    @AfterClass
    public void cancelActivation() throws InterruptedException {
        logger.info("After test started");
        activation = fetch.fetchByCreditId(activation.getCreditId());
        cancelService.cancelActivation(activation, isAutoClose);
    }
}
