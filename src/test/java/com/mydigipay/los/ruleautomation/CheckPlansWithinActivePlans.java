package com.mydigipay.los.ruleautomation;

import com.mydigipay.los.ruleautomation.model.group.Group;
import com.mydigipay.los.ruleautomation.model.plan.Plan;
import com.mydigipay.los.ruleautomation.model.plan.pojo.PlanDescription;
import com.mydigipay.los.ruleautomation.model.plan.pojo.PlanDetail;
import com.mydigipay.los.ruleautomation.model.plan.pojo.PlanRegistrationFlow;
import com.mydigipay.los.ruleautomation.model.user.User;
import com.mydigipay.los.ruleautomation.repository.GroupRepository;
import com.mydigipay.los.ruleautomation.service.ExcelController;
import com.mydigipay.los.ruleautomation.service.JsonController;
import com.mydigipay.los.ruleautomation.service.dashboardservices.WhiteList;
import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
public class CheckPlansWithinActivePlans extends AbstractTestNGSpringContextTests {


    @Autowired
    GroupRepository grpRepo;
    @Autowired
    WhiteList whiteListService;
    Group group;
    User testUser;
    RestUtil restUtil;
    private List<Plan> allActivePlans;
    private SoftAssert softAssert;
    @Value("${staging.credit-base-url-ms}")
    private String baseUrl;
    private String excelPath;


    @Parameters({"plansExcelFile", "testDataPath"})
    @BeforeClass
    private void testSetup(String plansExcelFile, String testDataPath) throws IOException {
        this.excelPath = plansExcelFile;
        softAssert = new SoftAssert();
        testUser = JsonController.jsonFileToPojoUsingKey(testDataPath, "user", User.class);

        restUtil = new RestUtil(baseUrl);
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Id", testUser.getUserId());
        headers.put("Content-Type", HttpHeader.contentType);
        headers.put("Digipay-Version", HttpHeader.digipayVersion);
        restUtil.headers(headers);


    }

    @DataProvider(name = "Read plans input")
    Plan[] readPlans() throws IOException, JSONException {

        Plan[] expectedPlans = ExcelController.readPlanFromExcel(excelPath);
        for (Plan plan : expectedPlans) {
            plan.setPlanRegistrationFlowDto(JsonController.propertyToPojo("PlanRegistrationFlow", PlanRegistrationFlow.class));
            plan.setDetails(JsonController.propertyToPojoList("ProviderPlanDetail" + plan.getFundProvider().getFundProviderCode(), PlanDetail.class));
            plan.getCollateralDto().setDescription(JsonController.propertyToPojo(plan.getCollateralDto().getType() + "Description", PlanDescription.class));
            group = grpRepo.findByGroupId(plan.getGroupId());
            if (group.getUserEntryPoint() == 4) {
                whiteListService.addToWhitelist(plan.getGroupId(), testUser.getCellNumber());
            }
        }
        String responseString = restUtil.path("/plans").get().getApiResponseAsString();
        JSONObject jsonResponse = new JSONObject(responseString);
        String plansListStr = jsonResponse.getJSONArray("planGroupDetails").toString();
        allActivePlans = JsonController.jsonToPojoList(plansListStr, Plan.class);
        return expectedPlans;
    }

    @Test(dataProvider = "Read plans input")
    void CheckPlanParams(Plan plan) {
        List<Plan> plansFoundForGroup;
        Plan foundPlan;
        String groupId = plan.getGroupId();
        plansFoundForGroup = allActivePlans.stream().filter(p -> groupId.equals(p.getGroupId()))
                .collect(Collectors.toList());
        int plansCount = plansFoundForGroup.size();
        Assert.assertEquals(plansCount, 1,
                "There should be exactly one plan for each group id! plans count is incorrect for this group:"
                        + groupId);
        foundPlan = plansFoundForGroup.get(0);
        // Check for fundprovider
        softAssert.assertEquals(foundPlan.getFundProvider().name, plan.getFundProvider().name,
                "For groupId" + groupId + "fund provider name is wrong!");
        softAssert.assertEquals(foundPlan.getFundProvider().fundProviderCode,
                plan.getFundProvider().fundProviderCode, "For groupId" + groupId + "fund provider code is wrong!");
        softAssert.assertEquals(foundPlan.getFundProvider().active, plan.getFundProvider().active,
                "For groupId" + groupId + "fund provider active status is wrong!");

        // Check for sumInstallmentAmount
        int amountThreshold = 10000;
        softAssert.assertTrue(
                foundPlan.getSumInstallmentAmount() - plan.getSumInstallmentAmount() < amountThreshold,
                "For groupId " + groupId + " sum installment amount is not in acceptable range!");
        // Check installment count
        softAssert.assertEquals(foundPlan.getInstallmentCount(), plan.getInstallmentCount(),
                "For groupId " + groupId + " installment count is wrong!");
        // Check installment amount
        softAssert.assertEquals(foundPlan.getInstallmentAmount(), plan.getInstallmentAmount(),
                "For groupId " + groupId + " installment amount is wrong!");

        // Check allocation prepayment
        softAssert.assertEquals(foundPlan.getAllocationPrepaymentAmount(), plan.getAllocationPrepaymentAmount(),
                "For groupId " + groupId + " allocation prepayment amount is wrong!");
        softAssert.assertTrue(foundPlan.getAllocationPrepaymentPercentage()  > plan.getAllocationPrepaymentPercentage(),
                "For groupId " + groupId + " allocation prepayment percentage is wrong!");
        softAssert.assertEquals(foundPlan.getAllocationPrepaymentPercentage(), plan.getAllocationPrepaymentPercentage(),
                "For groupId " + groupId + " allocation prepayment percentage is wrong!");
        // Check interest percentage
        softAssert.assertEquals(foundPlan.getInterestPercentage(), plan.getInterestPercentage(),
                "For groupId " + groupId + " allocation interest percentage is wrong!");

        // Check collatral amount
        softAssert.assertEquals(foundPlan.getCollateralAmount(), plan.getCollateralAmount(),
                "For groupId " + groupId + " allocation collatral amount is wrong!");
        // Check collatrall details
        softAssert.assertEquals(foundPlan.getCollateralDto(), plan.getCollateralDto(),
                "For groupId " + groupId + " collateral details are wrong!");

        // Check payable amount
        softAssert.assertTrue(foundPlan.getPayableAmount() - plan.getPayableAmount() < amountThreshold,
                "For groupId " + groupId + " payable amount is not in acceptable range!");

        // check plan activation
        softAssert.assertTrue(foundPlan.isActive(), "For groupId " + groupId + " plan is not active!");

        // Check filing payment amount
        softAssert.assertEquals(foundPlan.getFilingPaymentAmount(), plan.getFilingPaymentAmount(),
                "For groupId " + groupId + " filing payment is wrong!");

        // Check pre register delay
        softAssert.assertEquals(foundPlan.isPreRegisterWithDelay(), plan.isPreRegisterWithDelay(),
                "For groupId " + groupId + " pre register delay status is wrong!");

        // Check registration flow
        softAssert.assertEquals(foundPlan.getPlanRegistrationFlowDto(),
                plan.getPlanRegistrationFlowDto(), "For groupId " + groupId + " registration flow is wrong!");

        // Check plan details
        softAssert.assertEquals(foundPlan.getDetails(), plan.getDetails(),
                "For groupId " + groupId + " details are wrong!");

        // Check credit amount
        softAssert.assertEquals(foundPlan.getCreditAmount(), plan.getCreditAmount(),
                "For groupId " + groupId + " credit amount amount amount is wrong!");


        softAssert.assertAll();

    }
}
