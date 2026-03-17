package com.mydigipay.los.ruleautomation.service.stepservices.profile;

import com.mydigipay.los.ruleautomation.exception.ProfileException;
import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.model.address.Address;
import com.mydigipay.los.ruleautomation.model.user.User;
import com.mydigipay.los.ruleautomation.service.activationservice.FetchActivation;
import com.mydigipay.los.ruleautomation.utils.common.ConfigUtil;
import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;
import io.restassured.path.json.JsonPath;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class ProfileService {
    final
    ConfigUtil configUtil;
    final
    FetchActivation fetch;

    public ProfileService(ConfigUtil configUtil, FetchActivation fetch) {
        this.fetch = fetch;
        this.configUtil = configUtil;
    }

    private static Map<String, Object> getStringObjectMap(Address address) {
        Map<String, Object> body = new HashMap<>();
        body.put("postalCode", address.getPostalCode());
        body.put("provinceUid", address.getProvince().getUid());
        body.put("address", address.getAddress());
        body.put("addressNo", address.getAddressNo());
        body.put("addressUnit", address.getAddressUnit());
        body.put("phoneNumber", address.getPhoneNum());
        body.put("birthPlace", address.getBirthPlace().getUid());
        body.put("cityUid", address.getCity().getUid());
        return body;
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

    public Boolean readyToPostData(User user, Activation activation) throws ProfileException {
        JsonPath resp = prepareRestUtilInstance(user).pathParam("creditId", activation.getCreditId())
                .path("/activations/profile/status/{creditId}").get().getResponse().body().jsonPath();
        if (resp.getString("fields") != null) {
            throw new ProfileException(resp.getString("message"));
        }
        return resp.getString("status").equals("3") && resp.getString("fields") == null;
    }

    public void startProfileStep(User user, Activation activation) {
        prepareRestUtilInstance(user).pathParam("creditId", activation.getCreditId())
                .path("/activations/profile/in-progress/{creditId}").post();
    }

    public void updateProfile(User user, Activation activation, Address address) throws ProfileException {
        if (readyToPostData(user, activation)) {
            Map<String, Object> body = getStringObjectMap(address);
            prepareRestUtilInstance(user).pathParam("fpCode", activation.getGroup().getFundProvider().get("fpCode").toString())
                    .pathParam("creditId", activation.getCreditId())
                    .path("/users/profile/{fpCode}/{creditId}").body(body).post().getStatusCode();
        }


    }
}
