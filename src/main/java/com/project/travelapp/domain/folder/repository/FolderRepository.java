package com.project.travelapp.domain.folder.repository;

import com.project.travelapp.domain.folder.entity.Folder;
import com.project.travelapp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByUserOrderByCreatedAtDesc(User user);
}
