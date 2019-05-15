package com.seasun.management.mapper.dsp;

import com.seasun.management.model.ApDraw;
import com.seasun.management.model.dsp.BankCard;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BankCardMapper {

    @Select({"select * from BankCard"})
    List<BankCard> selectAll();
}
