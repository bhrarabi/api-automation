package com.mydigipay.los.ruleautomation.service.stepservices;
import com.mydigipay.los.ruleautomation.model.steps.Step;
import com.mydigipay.los.ruleautomation.model.steps.allocationPrepayment.AllocationPrepaymentStep;
import com.mydigipay.los.ruleautomation.model.steps.bankAccountVerification.BankAccountVerificationStep;
import com.mydigipay.los.ruleautomation.model.steps.checkCreditFile.SamatStep;
import com.mydigipay.los.ruleautomation.model.steps.cheque.ChequeStep;
import com.mydigipay.los.ruleautomation.model.steps.digitalSignature.DigitalSignatureStep;
import com.mydigipay.los.ruleautomation.model.steps.fillingPayment.FillingPaymentStep;
import com.mydigipay.los.ruleautomation.model.steps.promissoryNote.ENoteStep;
import com.mydigipay.los.ruleautomation.model.steps.signingDocuments.SigningDocumentsStep;
import com.mydigipay.los.ruleautomation.model.steps.walletActivaton.WalletActivatonStep;
import com.mydigipay.los.ruleautomation.repository.CreditProfileRepository;
import com.mydigipay.los.ruleautomation.repository.RuleRepository;
import com.mydigipay.los.ruleautomation.service.dashboardservices.DashboardLoginService;
import com.mydigipay.los.ruleautomation.service.dbservices.SqlExecuteQuery;
import com.mydigipay.los.ruleautomation.service.stepservices.alloctionPrepayment.AllocatioPrepaymentService;
import com.mydigipay.los.ruleautomation.service.stepservices.bankAccountVerification.BankAccountVerificationService;
import com.mydigipay.los.ruleautomation.service.stepservices.checkCreditFile.SamatService;
import com.mydigipay.los.ruleautomation.service.stepservices.cheque.ChequeService;
import com.mydigipay.los.ruleautomation.model.steps.profile.ProfileStep;
import com.mydigipay.los.ruleautomation.model.steps.scoring.BankScoringStep;
import com.mydigipay.los.ruleautomation.service.dbservices.MongoExecuteQuery;
import com.mydigipay.los.ruleautomation.service.stepservices.fillingPayment.FillingPaymentService;
import com.mydigipay.los.ruleautomation.service.stepservices.profile.ProfileService;
import com.mydigipay.los.ruleautomation.service.stepservices.scoring.ScoringService;
import com.mydigipay.los.ruleautomation.service.activationservice.FetchActivation;
import com.mydigipay.los.ruleautomation.service.stepservices.walletActivation.WalletActivationService;
import com.mydigipay.los.ruleautomation.utils.common.ConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
public class StepService {
    final
    FetchActivation fetch;
    final
    ConfigUtil configUtil;

    @Autowired
    MongoExecuteQuery execQuery;

    @Autowired
    CreditProfileRepository profileRepo;
    @Autowired
    SqlExecuteQuery sqlExecuteQuery;
    @Autowired
    RuleRepository ruleRepo;

    DashboardLoginService dashboardLoginService;

    public StepService(ConfigUtil configUtil, FetchActivation fetch, DashboardLoginService dashboardLoginService) {
        this.configUtil = configUtil;
        this.fetch = fetch;
        this.dashboardLoginService = dashboardLoginService;
    }

    public Step findStepByCode(String code) {
        switch (code) {
            case "PROFILE":
                ProfileStep createdProfileStep = new ProfileStep(new ProfileService(configUtil, fetch), this);
                createdProfileStep.setFetch(fetch);
                return createdProfileStep;
            case "BANK_SCORE_WITHOUT_PAY":
                BankScoringStep createdScoringStep = new BankScoringStep(new ScoringService(configUtil, fetch, execQuery), this);
                createdScoringStep.setFetch(fetch);
                return createdScoringStep;
            case "CHEQUE_UPLOAD":
                ChequeStep createChequeStep = new ChequeStep(new ChequeService(configUtil, fetch, dashboardLoginService, sqlExecuteQuery), this);
                createChequeStep.setFetch(fetch);
                return createChequeStep;
            case "FILING_PAYMENT":
                FillingPaymentStep createFillingPaymentStep = new FillingPaymentStep(new FillingPaymentService(configUtil, fetch), this);
                createFillingPaymentStep.setFetch(fetch);
                return createFillingPaymentStep;
            case "CHECK_CREDIT_FILE":
                SamatStep createSamatStep = new SamatStep(new SamatService(configUtil, fetch, execQuery), this);
                createSamatStep.setFetch(fetch);
                return createSamatStep;
            case "WALLET_ACTIVATION":
                WalletActivatonStep createWalletActivationStep = new WalletActivatonStep(new WalletActivationService(configUtil, fetch), this);
                createWalletActivationStep.setFetch(fetch);
                return createWalletActivationStep;
            case "ALLOCATION_PREPAYMENT":
                AllocationPrepaymentStep createAllocatioPrepaymentStep = new AllocationPrepaymentStep(new AllocatioPrepaymentService(configUtil, fetch, dashboardLoginService, ruleRepo,profileRepo, execQuery), this);
                createAllocatioPrepaymentStep.setFetch(fetch);
                return createAllocatioPrepaymentStep;
            case "DIGITAL_SIGNATURE":
                DigitalSignatureStep createDigitalSignStep = new DigitalSignatureStep(this);
                createDigitalSignStep.setFetch(fetch);
                return createDigitalSignStep;
            case "E_NOTE":
                ENoteStep createEnoteStep = new ENoteStep(this);
                createEnoteStep.setFetch(fetch);
                return createEnoteStep;
            case "SIGNING_DOCUMENT":
                SigningDocumentsStep createSignDocStep = new SigningDocumentsStep(this);
                createSignDocStep.setFetch(fetch);
                return createSignDocStep;
            case "BANK_ACCOUNT_VERIFICATION":
                BankAccountVerificationStep createBankAcountStep = new BankAccountVerificationStep(new BankAccountVerificationService(configUtil,fetch),this);
                createBankAcountStep.setFetch(fetch);
                return createBankAcountStep;

        }

        return null;
    }

    public void setStatus(String creditId, String status) {
        execQuery.setDbName(configUtil.getProperty("databases.switch-credit.name"));
        execQuery.setCollectionName(configUtil.getProperty("databases.switch-credit.collections.activationCol"));

        String query = "{\n" +
                "\t\"findQuery\": {\n" +
                "\t\"creditId\": \"" + creditId + "\"\n" +
                "\t},\n" +
                "\t\"updateQuery\": {\n" +
                "\t\"$set\": {\n" +
                "\t\"status\": \"" + status + "\"\n" +
                "\t}\n" +
                "\t}\n" +
                "}";
        execQuery.updateOne(query);
    }
    public void setCompleteDate(String creditId, String stepCode){
        execQuery.setDbName(configUtil.getProperty("databases.switch-credit.name"));
        execQuery.setCollectionName(configUtil.getProperty("databases.switch-credit.collections.activationCol"));
        Instant instant = Instant.now();
        String query = "{\n" +
                "\"findQuery\":{\"creditId\":\""+creditId+"\", \"steps.code\": \""+stepCode+"\"},\"updateQuery\":{\"$set\": { \"steps.$.completedDate\": "+instant.toEpochMilli()+"}}\n" +
                "}";
        execQuery.updateOne(query);
    }

}

