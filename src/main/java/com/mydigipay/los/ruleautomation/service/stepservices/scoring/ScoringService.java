package com.mydigipay.los.ruleautomation.service.stepservices.scoring;

import com.mydigipay.los.ruleautomation.exception.ScoringException;
import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.model.user.User;
import com.mydigipay.los.ruleautomation.service.activationservice.FetchActivation;
import com.mydigipay.los.ruleautomation.service.dbservices.MongoExecuteQuery;
import com.mydigipay.los.ruleautomation.utils.common.ConfigUtil;
import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;
import io.restassured.response.Response;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@Service
public class ScoringService {

    final
    ConfigUtil configUtil;
    final
    FetchActivation fetch;
    private final MongoExecuteQuery mongoService;
//    @Value("${scoreDetail.icsScore}")
    @Setter
     private int icsScore;
//   @Value("${scoreDetail.bankScore}")

    @Setter
     private int bankScore;

    public ScoringService(ConfigUtil configUtil, FetchActivation fetch, MongoExecuteQuery mongoService) {
        this.configUtil = configUtil;
        this.fetch = fetch;
        this.mongoService = mongoService;

    }

    private RestUtil prepareRestUtilInstance(User user) {

        RestUtil request = new RestUtil(configUtil.getProperty("staging.credit-base-url-ms"));
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Id", user.getUserId());
        headers.put("Content-Type", HttpHeader.contentType);
        headers.put("Digipay-Version", HttpHeader.digipayVersion);
        headers.put("agent", HttpHeader.agent);
        return request.headers(headers);
    }

    private Boolean checkInBlacklist(Activation activation, User user) {
        Response resp = prepareRestUtilInstance(user).pathParam("creditId", activation.getCreditId())
                .path("/scores/check-blacklist/{creditId}").post().getResponse();
        String status = resp.body().jsonPath().getString("result.title");

        return status.equals("SUCCESS");
    }

    public void setScoringHistory(User user, int score) {
        mongoService.setDbName(configUtil.getProperty("databases.credit-score.name"));
        mongoService.setCollectionName(configUtil.getProperty("databases.credit-score.collections.icsCol"));
        Instant instant = Instant.now();
        String body = "{\n" +
                "\t\"findQuery\": {\n" +
                "\t\t\"initiator\": \"ruleAutomationTest\"\n" +
                "\t},\n" +
                "\t\"updateQuery\": {\n" +
                "\t\t\"$set\": {\n" +
                "\t\t\t\"owner.cellNumber\": \"" + user.getCellNumber() + "\"\n" +
                "            , \"score\":" + score +
                "            ,  \"owner.nationalCode\":\"" + user.getNationalCode() + "\",\n" +
                "    \"creationDate\":" + instant.toEpochMilli() + "\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";
        mongoService.updateOne(body);

    }

    public void setBankScoreHistory(User user, int score) {
        mongoService.setDbName(configUtil.getProperty("databases.credit-score.name"));
        mongoService.setCollectionName(configUtil.getProperty("databases.credit-score.collections.bankScore"));

        Instant instant = Instant.now();
        String body = "{\n" +
                "\t\"findQuery\": {\n" +
                "\t\t\"initiator\": \"ruleAutomationTest\"\n" +
                "\t},\n" +
                "\t\"updateQuery\": {\n" +
                "\t\t\"$set\": {\n" +
                "             \"owner.nationalCode\":\"" + user.getNationalCode() + "\",\n" +
                "\"score\":" + score +
                "    , \"creationDate\":" + instant.toEpochMilli() + "\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";
        mongoService.updateOne(body);

    }

    public void checkUserEligibility(Activation activation, User user) throws ScoringException {
        if (checkInBlacklist(activation, user)) {
            setScoringHistory(user, icsScore);
            setBankScoreHistory(user, bankScore);
        } else {
            throw new ScoringException("User is in blacklist!");
        }
    }

    private void initScoring(Activation activation, User user) {
       prepareRestUtilInstance(user).pathParam("creditId", activation.getCreditId())
                .path("/scores/init/{creditId}").post();
    }

    private void scoreInquiry(Activation activation, User user) {
        prepareRestUtilInstance(user).pathParam("creditId", activation.getCreditId())
                .path("/scores/inquiry/{creditId}").post();
    }

    public void processScoring(Activation activation, User user) {
        initScoring(activation, user);
        scoreInquiry(activation, user);
    }
}
