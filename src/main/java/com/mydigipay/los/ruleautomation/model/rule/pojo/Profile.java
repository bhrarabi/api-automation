package com.mydigipay.los.ruleautomation.model.rule.pojo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "profiles")
public class Profile {
    private String field;
}
