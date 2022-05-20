package com.jch.mybatisplus;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("t_jpa")
@Accessors(chain = true)
public class MybatisEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    @TableField
    private String name;
    @TableField
    private Date createTime;
    @TableField
    private Date updateTime;
}
