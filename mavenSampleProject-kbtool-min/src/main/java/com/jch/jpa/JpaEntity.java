package com.jch.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class JpaEntity {
	@GeneratedValue
	@Id
	UUID uuid;
	String name;
}
