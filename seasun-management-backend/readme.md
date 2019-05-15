# 后端代码工程
eclipse构建方法：
gradle eclipse
(注：首次编译，需手动配置resource模块属性，方法：
file - properties - source - add folder - src/main/resource)

idea构建方法：
gradle idea
（注：首次编译，需手动配置resource模块属性，方法：
file-project structure-modules-src-main-resource,将其标记为sources即可）

## trouble-shotting:
- idea中classPath不生效。
检查：file-project structure-module，resource是否标记为source状态 , 若没有，则resource下的properties文件无法拷贝到out目录下，导致配置文件读取失败，程序启动找不到datasource

- idea连接mysql，插入的中文编码错误。
检查：数据库编码，table编码 均为utf-8, 
     properties的连接编码为utf-8
     idea-ide中，file-editor-encoding. ide-encoding+project-encoding+default-properties-file-encoding 均为utf-8

## coding-relative
-- mapper层 
   updateByPrimaryKeySelective(Model model) :只获取model中不为空的字段 
   updateByPrimaryKey(Model model) ：即使字段为空，也全部更新。
   updateByXXXExample(XXExample example) : 使用criteria动态查询

