package com.seasun.management.service.kingsoftLife;


import com.seasun.management.vo.KsLifeCommonVo;

public interface KsUserService {

    Object getUserSeatNo(String loginId);

    KsLifeCommonVo unbindSeatNo();

    KsLifeCommonVo bindSeatNo(String seatNo);

    KsLifeCommonVo getAvatar(String loginId);
}
