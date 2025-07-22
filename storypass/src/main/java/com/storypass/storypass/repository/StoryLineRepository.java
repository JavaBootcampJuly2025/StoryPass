package com.storypass.storypass.repository;

import com.storypass.storypass.model.StoryLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryLineRepository extends JpaRepository<StoryLine, Long> {
}