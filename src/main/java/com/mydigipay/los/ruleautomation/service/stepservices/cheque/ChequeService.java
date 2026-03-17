package com.mydigipay.los.ruleautomation.service.stepservices.cheque;

import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.model.activation.pojo.StepCode;
import com.mydigipay.los.ruleautomation.model.steps.cheque.pojo.ChequeAdminRequestModel;
import com.mydigipay.los.ruleautomation.model.steps.cheque.pojo.ChequeDetailRequestModel;
import com.mydigipay.los.ruleautomation.model.user.User;
import com.mydigipay.los.ruleautomation.service.activationservice.FetchActivation;
import com.mydigipay.los.ruleautomation.service.converter.CustomDateConverter;
import com.mydigipay.los.ruleautomation.service.dashboardservices.DashboardLoginService;
import com.mydigipay.los.ruleautomation.service.dbservices.SqlExecuteQuery;
import com.mydigipay.los.ruleautomation.utils.common.ConfigUtil;
import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Service
/*
 * Author: b.arabi
 */
public class ChequeService {

    final DashboardLoginService dashboardLoginService;
    ConfigUtil configUtil;
    SqlExecuteQuery executeQuery;
    FetchActivation fetch;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> dashboardHeaders = new HashMap<>();
    private RestUtil request;
    private ChequeDetailRequestModel chequeDetailRequestModel;
    private Activation activation;
    private User user;
    private ChequeAdminRequestModel chequeAdminRequest;


    public ChequeService(ConfigUtil configUtil, FetchActivation fetch, DashboardLoginService dashboardLoginService, SqlExecuteQuery executeQuery) {
        this.configUtil = configUtil;
        this.executeQuery = executeQuery;
        this.fetch = fetch;
        this.dashboardLoginService = dashboardLoginService;
    }


    public void setupRequest() {
        request = RestUtil.init(configUtil.getProperty("staging.credit-base-url-ms"));
        headers.put("User-Id", user.getUserId());
        headers.put("Content-Type", HttpHeader.contentType);
        headers.put("Digipay-Version", HttpHeader.digipayVersion);
        headers.put("agent", HttpHeader.agent);
        String adminToken = dashboardLoginService.getToken();
        dashboardHeaders.put("Authorization", "Bearer " + adminToken);
        dashboardHeaders.put("Content-Type", HttpHeader.contentType);
        chequeAdminRequest = new ChequeAdminRequestModel();
        chequeAdminRequest.setStepTag(String.valueOf(StepCode.CHEQUE_UPLOAD.getValue()));
        chequeAdminRequest.setFundProviderCode(activation.getGroup().getFundProvider().get("fpCode").toString());
        chequeAdminRequest.setCreditId(activation.getCreditId());
        chequeAdminRequest.setUserId(user.getUserId());

    }

    public void postChequeDetail(ChequeDetailRequestModel chequeDetailRequestModel) {
        deleteRedundantData(chequeDetailRequestModel.getChequeId());
        request = RestUtil.init(configUtil.getProperty("staging.credit-base-url-ms"))
                .headers(headers)
                .body(chequeDetailRequestModel)
                .pathParam("fundProviderCode", activation.getGroup().getFundProvider().get("fpCode").toString())
                .pathParam("creditId", activation.getCreditId())
                .path("/cheques/{fundProviderCode}/{creditId}")
                .post();

    }

    public void uploadDocument() {
        headers.remove("Content-Type");
        request = RestUtil.init(configUtil.getProperty("staging.credit-base-url-ms"))
                .headers(headers)
                .multiPart("file", FileUtils.getFile(Objects.requireNonNull(getClass().getClassLoader().getResource(configUtil.getProperty("los.cheque-image-path"))).getFile()))
                .pathParam("fundProviderCode", activation.getGroup().getFundProvider().get("fpCode").toString())
                .pathParam("creditId", activation.getCreditId())
                .path("/documents/{fundProviderCode}/{creditId}/8")
                .post();
    }

    public void chequeConfirm() {
        headers.put("Content-Type", HttpHeader.contentType);
        request = RestUtil.init(configUtil.getProperty("staging.credit-base-url-ms"))
                .headers(headers)
                .pathParam("fundProviderCode", activation.getGroup().getFundProvider().get("fpCode").toString())
                .pathParam("creditId", activation.getCreditId())
                .path("/cheques/confirm/{fundProviderCode}/{creditId}")
                .post();
    }

    public void adminChequeAccept() {
        request = RestUtil.init(configUtil.getProperty("staging.dashboard-base-url"))
                .headers(dashboardHeaders)
                .path("credit/activations/admin/document/accept")
                .body(chequeAdminRequest)
                .post();

    }

    public void adminChequeReceive() {
        request = RestUtil.init(configUtil.getProperty("staging.dashboard-base-url"))
                .headers(dashboardHeaders)
                .path("credit/admin/cheques/received")
                .body(chequeAdminRequest)
                .post();
    }

    public void adminChequeApprove() {
        request = RestUtil.init(configUtil.getProperty("staging.dashboard-base-url"))
                .headers(dashboardHeaders)
                .path("credit/activations/admin/documents/approve")
                .body(chequeAdminRequest)
                .post();
    }

    public ChequeDetailRequestModel setCheckDetails(User user, ChequeDetailRequestModel chequeDetailRequestModel) throws ParseException {

        chequeDetailRequestModel.setOwnerCellNumber(user.getCellNumber());
        chequeDetailRequestModel.setOwnerName(user.getName() + " " + user.getSureName());
        chequeDetailRequestModel.setOwnerNationalCode(user.getNationalCode());
        chequeDetailRequestModel.setOwnerBirthDate(CustomDateConverter.jalaliToTimeStamp(user.getBirthDate()));
        return chequeDetailRequestModel;
    }

    private void deleteRedundantData(String chequeId) {
        String query = "update  switch_credit_db.cheque_details set cheque_id='" + chequeId + "0' where cheque_id='" + chequeId + "'";
       executeQuery.execute(query);


    }
}
