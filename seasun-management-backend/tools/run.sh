#!/bin/bash

rm -rf others/mapper
rm -rf others/com
java -jar mybatis-generator-core-1.3.5.jar -configfile ./generatorConfig.xml -overwrite

