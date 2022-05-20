package com.jch.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaRepo extends JpaRepository<JpaEntity, UUID> {
}
