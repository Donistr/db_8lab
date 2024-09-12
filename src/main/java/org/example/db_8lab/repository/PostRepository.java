package org.example.db_8lab.repository;

import org.example.db_8lab.entity.Post;
import org.example.db_8lab.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findAllByGroup(UserGroup group);

}
