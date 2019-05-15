package com.seasun.management.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import com.seasun.management.model.ApDanmaku;

public interface ApDanmakuMapper {

    @Insert({
            "insert into ap_danmaku (year, user_id, user_name, danmaku, show_flag, create_time, update_time) ",
            "values (#{year,jdbcType=VARCHAR}, #{userId,jdbcType=BIGINT}, #{userName,jdbcType=VARCHAR}, ",
            "#{danmaku,jdbcType=VARCHAR}, #{showFlag, jdbcType=TINYINT}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(ApDanmaku apDanmaku);

    @Select({"select * from ap_danmaku where show_flag = 0"})
	List<ApDanmaku> selectNotShowed();
	
    @Select({"update ap_danmaku set show_flag = 1 where show_flag = 0"})
	List<ApDanmaku> updateShowed();
}
