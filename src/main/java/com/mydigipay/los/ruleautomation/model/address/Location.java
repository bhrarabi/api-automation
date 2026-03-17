package com.mydigipay.los.ruleautomation.model.address;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
/*
 * Author: f.bahramnejad
 */
public class Location {
    String uid;

    @JsonCreator
    public Location(@JsonProperty("uid") String uid) {
        this.uid = uid;
    }

}
