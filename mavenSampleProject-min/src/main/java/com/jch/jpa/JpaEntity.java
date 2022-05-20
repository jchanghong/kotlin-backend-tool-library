package com.jch.jpa;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Data
@Accessors(chain = true)
@Table(name = "t_jpa")
public class JpaEntity {
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;
	@Column
	@NotBlank
	private String name;
	@Column
	@CreationTimestamp
	private Date createTime;
	@UpdateTimestamp
	private Date updateTime;
}
