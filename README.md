#废旧物资价格数据采集项目简介 

每天从中国废品网采集废旧物资价格数据。
项目中有两个定时任务：CollectTask，LoadTask，分别采集废旧物资价格html页面，解析html页面并将数据保存到数据库

##技术栈 

spring cloud、druid、lombok、jsoup、joda-time、MySQL

##主要功能

1、采集中国废品网(http://www.zgfp.com)的废旧物资价格

##使用说明 

1、创建数据库及相关表（建表语句见init.sql文件），然后配置application.properties中数据库连接 

2、配置保存html文件的目录，即application.properties文件中的download.task.savePath 

3、运行StartApplication中的main方法，启动项目 
