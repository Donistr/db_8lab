package org.example.db_8lab.repository;

import org.example.db_8lab.entity.Comment;
import org.example.db_8lab.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByPost(Post post);

}
