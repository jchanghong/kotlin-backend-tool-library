package com.github.jchanghong.test

/*
*
date  2020-10-16
superServiceImplClassPackage  com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
baseResultMap  false
superMapperClass  BaseMapper
activeRecord  false
superServiceClass  IService
controllerMappingHyphenStyle  true
controllerMappingHyphen  check-org
superServiceImplClass  ServiceImpl
table  TableInfo(importPackages=[com.baomidou.mybatisplus.annotation.TableName, com.baomidou.mybatisplus.annotation.IdType, com.baomidou.mybatisplus.annotation.TableId, java.time.LocalDateTime, com.baomidou.mybatisplus.annotation.TableField, java.io.Serializable], convert=true, name=check_org, comment=, entityName=CheckOrg, mapperName=CheckOrgMapper, xmlName=CheckOrgMapper, serviceName=ICheckOrgService, serviceImplName=CheckOrgServiceImpl, controllerName=CheckOrgController, fields=[TableField(convert=true, keyFlag=true, keyIdentityFlag=false, name=org_id, type=integer, propertyName=orgId, columnType=INTEGER, comment=, fill=null, keyWords=false, columnName=org_id, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=parent_id, type=integer, propertyName=parentId, columnType=INTEGER, comment=, fill=null, keyWords=false, columnName=parent_id, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=org_name, type=character varying(225), propertyName=orgName, columnType=STRING, comment=, fill=null, keyWords=false, columnName=org_name, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=dbtype, type=integer, propertyName=dbtype, columnType=INTEGER, comment=1 oracle  2 postgresql, fill=null, keyWords=false, columnName=dbtype, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=dbuser, type=character varying(100), propertyName=dbuser, columnType=STRING, comment=, fill=null, keyWords=false, columnName=dbuser, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=dburl, type=character varying(100), propertyName=dburl, columnType=STRING, comment=, fill=null, keyWords=false, columnName=dburl, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=dbpassword, type=character varying(100), propertyName=dbpassword, columnType=STRING, comment=, fill=null, keyWords=false, columnName=dbpassword, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=jobtime, type=timestamp without time zone, propertyName=jobtime, columnType=LOCAL_DATE_TIME, comment=, fill=null, keyWords=false, columnName=jobtime, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=cms_cascade_id, type=integer, propertyName=cmsCascadeId, columnType=INTEGER, comment=, fill=null, keyWords=false, columnName=cms_cascade_id, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=login_user, type=character varying(255), propertyName=loginUser, columnType=STRING, comment=, fill=null, keyWords=false, columnName=login_user, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=login_password, type=character varying(255), propertyName=loginPassword, columnType=STRING, comment=, fill=null, keyWords=false, columnName=login_password, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=sort_id, type=integer, propertyName=sortId, columnType=INTEGER, comment=, fill=null, keyWords=false, columnName=sort_id, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=xulie, type=integer, propertyName=xulie, columnType=INTEGER, comment=, fill=null, keyWords=false, columnName=xulie, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=police_number, type=integer, propertyName=policeNumber, columnType=INTEGER, comment=, fill=null, keyWords=false, columnName=police_number, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=ip, type=character varying(255), propertyName=ip, columnType=STRING, comment=, fill=null, keyWords=false, columnName=ip, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=index_code56, type=character varying(10), propertyName=indexCode56, columnType=STRING, comment=, fill=null, keyWords=false, columnName=index_code56, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=aic_name, type=character varying(255), propertyName=aicName, columnType=STRING, comment=, fill=null, keyWords=false, columnName=aic_name, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=db_url9200, type=character varying, propertyName=dbUrl9200, columnType=STRING, comment=, fill=null, keyWords=false, columnName=db_url9200, customMap=null), TableField(convert=true, keyFlag=false, keyIdentityFlag=false, name=aic_cms, type=character varying, propertyName=aicCms, columnType=STRING, comment=, fill=null, keyWords=false, columnName=aic_cms, customMap=null)], havePrimaryKey=true, commonFields=[], fieldNames=org_id, parent_id, org_name, dbtype, dbuser, dburl, dbpassword, jobtime, cms_cascade_id, login_user, login_password, sort_id, xulie, police_number, ip, index_code56, aic_name, db_url9200, aic_cms)
package  {Entity=com.baomidou.ant.check.entity, Mapper=com.baomidou.ant.check.mapper, ModuleName=check, Xml=com.baomidou.ant.check.mapper.xml, ServiceImpl=com.baomidou.ant.check.service.impl, Service=com.baomidou.ant.check.service, Controller=com.baomidou.ant.check.controller}
idType  AUTO
author  jobob
swagger2  true
baseColumnList  false
kotlin  true
entityLombokModel  false
superMapperClassPackage  com.baomidou.mybatisplus.core.mapper.BaseMapper
restControllerStyle  true
chainModel  false
entityBuilderModel  false
superServiceClassPackage  com.baomidou.mybatisplus.extension.service.IService
entitySerialVersionUID  true
entityBooleanColumnRemoveIsPrefix  false
entityColumnConstant  false
config  com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder@cad498c
enableCache  false
entity  CheckOrg
* */