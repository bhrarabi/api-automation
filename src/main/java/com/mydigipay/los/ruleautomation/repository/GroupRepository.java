package com.mydigipay.los.ruleautomation.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mydigipay.los.ruleautomation.model.group.Group;

@Repository
public interface GroupRepository extends CrudRepository<Group, String> {
    Group findByGroupId(String groupId);

    Group findByTitle(String title);

    Group findByRuleId(String ruleId);
}
