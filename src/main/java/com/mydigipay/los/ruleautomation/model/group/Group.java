package com.mydigipay.los.ruleautomation.model.group;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
@Table(name = "groups")
public class Group {

    @Id
    @Column(name = "group_id")
    private String groupId;
    @Getter
    @Column(name = "user_entry_point")
    private int userEntryPoint;

    @Column(name = "rule_id")
    private String ruleId;

    private String title;

}
