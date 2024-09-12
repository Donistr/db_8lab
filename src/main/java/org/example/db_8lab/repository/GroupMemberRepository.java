package org.example.db_8lab.repository;

import org.example.db_8lab.entity.GroupMember;
import org.example.db_8lab.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {

    List<GroupMember> findAllByGroup(UserGroup group);

}
