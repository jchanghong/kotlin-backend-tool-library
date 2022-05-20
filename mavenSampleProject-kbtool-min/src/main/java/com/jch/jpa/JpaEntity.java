package com.jch.jpa;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.AbstractAuditable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Data
@Accessors(chain = true)
@Table(name = "t_jpa")
public class JpaEntity extends AbstractAuditable<String,UUID> {
	@GeneratedValue
	@Id
	UUID uuid;
	String name;
}
