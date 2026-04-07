package com.example.Project400Backend.Repositories;

import com.example.Project400Backend.Models.User;
import com.example.Project400Backend.Models.WeightEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeightEntryRepository extends JpaRepository<WeightEntry, Long> {
    List<WeightEntry> findByUserOrderByEntryDateAsc(User user);
    List<WeightEntry> findByUserOrderByEntryDateDesc(User user);
}