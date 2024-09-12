package org.example.db_8lab.repository;

import org.example.db_8lab.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {

    List<UserGroup> findAllByName(String name);

}
