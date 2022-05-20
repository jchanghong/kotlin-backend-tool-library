package com.jch.jpa;

import java.util.UUID;

public interface JpaRepository extends org.springframework.data.jpa.repository.JpaRepository<JpaEntity, UUID> {
}
