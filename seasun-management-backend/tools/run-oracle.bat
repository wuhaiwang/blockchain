@ECHO OFF

Set dir1=%cd%\others\com\seasun\management\mapper
Set dir2=%cd%\others\com\seasun\management\model
Set dir3=%cd%\others\mapper

Echo Deleting all files from %dir1%
del %dir1%\*.* /F /Q

Echo Deleting all files from %dir2%
del %dir2%\*.* /F /Q

Echo Deleting all files from %dir3%
del %dir3%\*.* /F /Q

java -jar mybatis-generator-core-1.3.5.jar -configfile ./generatorConfig-oracle.xml -overwrite
