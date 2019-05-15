package com.seasun.management.service;

import com.seasun.management.Application;
import com.seasun.management.mapper.UserDormitoryReservationMapper;
import com.seasun.management.service.kingsoftLife.KsDormitoryService;
import com.seasun.management.service.kingsoftLife.KsDormitoryServiceImpl;
import com.seasun.management.vo.BaseQueryResponseVo;
import com.seasun.management.vo.KsDormitoryReserveVo;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class KsDormitoryServiceTest extends TestCase {

    @Autowired
    KsDormitoryService ksDormitoryService;

    @Autowired
    UserDormitoryReservationMapper userDormitoryReservationMapper;

    Logger logger = LoggerFactory.getLogger(KsDormitoryServiceTest.class);

    /**
     * 测试输入查询条件,输入所有的列
     * 期望输出: 产生excel表格, 一共17行
     * */
    @Test
    public void testExportDormitoryReserveInfo () throws IOException {
        /**
         * mock 数据
         * */

        List<KsDormitoryReserveVo> ksDormitoryReserveVoList = new ArrayList<>(16);

        for (Integer i=0; i<16; i++) {
            KsDormitoryReserveVo ksDormitoryReserveVo = new KsDormitoryReserveVo();
            ksDormitoryReserveVo.setRoomNo("0");
            ksDormitoryReserveVo.setBedroom("1");
            ksDormitoryReserveVo.setBerth("2");
            ksDormitoryReserveVo.setName("3");
            ksDormitoryReserveVo.setIdCode("4");
            ksDormitoryReserveVo.setMobile("5");
            ksDormitoryReserveVo.setLiveState("6");
            ksDormitoryReserveVo.setExpectCheckInDate("7");
            ksDormitoryReserveVo.setExpectCheckOutDate("8");
            ksDormitoryReserveVo.setCheckInDate("9");
            ksDormitoryReserveVo.setCheckOutDate("10");
            ksDormitoryReserveVo.setBookBy("11");
            ksDormitoryReserveVoList.add (ksDormitoryReserveVo);
        }

        LinkedHashSet set = new LinkedHashSet();

        KsDormitoryServiceImpl.cellNameMap.keySet().stream().forEach(key -> {
            set.add (key);
        });


        String path = ksDormitoryService.exportExcel(set,ksDormitoryReserveVoList );
        logger.debug ("responseEntity -> {}", path);
    }


    /**
     * 测试输入查询条件,输入部分的列, 均存在  roomNo, bedroom, idCode, liveState
     *
     * */
    @Test
    public void testOptionExportDormitoryReserveInfo () throws IOException {

        String fields[] = {"roomNo", "bedroom", "idCode", "liveState"};
        LinkedHashSet<String> set = new LinkedHashSet<>(Arrays.asList(fields));
        List<KsDormitoryReserveVo> ksDormitoryReserveVoList = new ArrayList<>(16);

        for (Integer i=0; i<16; i++) {
            KsDormitoryReserveVo ksDormitoryReserveVo = new KsDormitoryReserveVo();
            ksDormitoryReserveVo.setRoomNo("0");
            ksDormitoryReserveVo.setBedroom("1");
            ksDormitoryReserveVo.setBerth("2");
            ksDormitoryReserveVo.setName("3");
            ksDormitoryReserveVo.setIdCode("4");
            ksDormitoryReserveVo.setMobile("5");
            ksDormitoryReserveVo.setLiveState("6");
            ksDormitoryReserveVo.setExpectCheckInDate("7");
            ksDormitoryReserveVo.setExpectCheckOutDate("8");
            ksDormitoryReserveVo.setCheckInDate("9");
            ksDormitoryReserveVo.setCheckOutDate("10");
            ksDormitoryReserveVo.setBookBy("11");
            ksDormitoryReserveVoList.add (ksDormitoryReserveVo);
        }

        String path = ksDormitoryService.exportExcel(set, ksDormitoryReserveVoList );
        logger.debug ("responseEntity -> {}", path);
    }



    /**
     * 测试输入查询条件,输入部分的列, 部分存在  roomNo, bedroom, idCode, liveState, "id_code", "Name"
     * 输出: 4列
     * */
    @Test
    public void testOptionAndNotExisitsExportDormitoryReserveInfo () throws IOException {

        String fields[] = {"roomNo","bedroom","berth","name","idCode","mobile","liveState","expectCheckInDate","expectCheckOutDate","checkInDate","checkOutDate","bookBy"};
        LinkedHashSet<String> set = new LinkedHashSet<>(Arrays.asList(fields));

        List<KsDormitoryReserveVo> ksDormitoryReserveVoList = new ArrayList<>(16);

        for (Integer i=0; i<16; i++) {
            KsDormitoryReserveVo ksDormitoryReserveVo = new KsDormitoryReserveVo();
            ksDormitoryReserveVo.setRoomNo("0");
            ksDormitoryReserveVo.setBedroom("1");
            ksDormitoryReserveVo.setBerth("2");
            ksDormitoryReserveVo.setName("3");
            ksDormitoryReserveVo.setIdCode("4");
            ksDormitoryReserveVo.setMobile("5");
            ksDormitoryReserveVo.setLiveState("6");
            ksDormitoryReserveVo.setExpectCheckInDate("7");
            ksDormitoryReserveVo.setExpectCheckOutDate("8");
            ksDormitoryReserveVo.setCheckInDate("9");
            ksDormitoryReserveVo.setCheckOutDate("10");
            ksDormitoryReserveVo.setBookBy("11");
            ksDormitoryReserveVoList.add (ksDormitoryReserveVo);
        }

        String path = ksDormitoryService.exportExcel(set, ksDormitoryReserveVoList );
        logger.debug ("responseEntity -> {}", path);
    }



    /**
     *
     * */
    @Test
    public void testGetKsDormitoryReserveVoDeclareFieldsValue () throws IOException {

        List<String> fields = KsDormitoryServiceImpl.cellNameMap.keySet().stream().collect(Collectors.toList());

        logger.debug ("fields -> {}", fields);

        assertEquals("roomNo", fields.get(0));
        assertEquals("bedroom", fields.get(1));
        assertEquals("berth", fields.get(2));
        assertEquals("name", fields.get(3));
        assertEquals("idCode", fields.get(4));
        assertEquals("mobile", fields.get(5));
        assertEquals("liveState", fields.get(6));
        assertEquals("expectCheckInDate", fields.get(7));
        assertEquals("expectCheckOutDate", fields.get(8));
        assertEquals("checkInDate", fields.get(9));
        assertEquals("checkOutDate", fields.get(10));
        assertEquals("bookBy", fields.get(11));
        /**
         * mock 数据
         * */
        KsDormitoryReserveVo ksDormitoryReserveVo = new KsDormitoryReserveVo();
        ksDormitoryReserveVo.setRoomNo("0");
        ksDormitoryReserveVo.setBedroom("1");
        ksDormitoryReserveVo.setBerth("2");
        ksDormitoryReserveVo.setName("3");
        ksDormitoryReserveVo.setIdCode("4");
        ksDormitoryReserveVo.setMobile("5");
        ksDormitoryReserveVo.setLiveState("6");
        ksDormitoryReserveVo.setExpectCheckInDate("7");
        ksDormitoryReserveVo.setExpectCheckOutDate("8");
        ksDormitoryReserveVo.setCheckInDate("9");
        ksDormitoryReserveVo.setCheckOutDate("10");
        ksDormitoryReserveVo.setBookBy("11");

        List<String> values = ksDormitoryService.getKsDormitoryReserveVoDeclareFieldsValue(fields, ksDormitoryReserveVo);
        logger.info ("values -> {}", values);
        for (Integer i =0; i< values.size();i++) {
           assertEquals(i+"", values.get(i));
        }
        assertEquals(12, values.size());

        logger.debug ("values -> {}", values);


    }

    /**
     * 测试根据订单号查询入住信息
     * 输入: '2018040412461741909612' , '2018040417593911481060'
     * expect size = 2
     * */
    @Test
    public void testGetDormitoryReserveByLiveNo() {
        String liveNos[]= {"2018040417593911481060", "2018041614324810932748"};
        BaseQueryResponseVo vo =  ksDormitoryService.getDormitoryReserveInfoByLiveNos(Arrays.asList(liveNos),20);
        List list = (List)vo.getItems();
        assertEquals(2,list.size() );
    }

    @Test
    public void testGetDormitoryReserveByLiveNoAsync () throws ExecutionException, InterruptedException {

        List<String> liveNos = userDormitoryReservationMapper.selectAll().stream().map(item->{
            return item.getLiveNo();
        }).collect(Collectors.toList());
        liveNos = liveNos.subList(0,9);
        List<List<String>> list = new ArrayList<>();
        List<String> l = null;
        for (int i=0; i<liveNos.size();i++) {
            if (i%5==0) {
                l = new ArrayList<>(5);
                list.add (l);
            }
            l.add (liveNos.get(i));
        }

        logger.debug ("list -> {}", list);
        for (List<String> list1 : list){
            Future<BaseQueryResponseVo> future = ksDormitoryService.getKsDormitoryReserveVoAsync(list1,5);
            BaseQueryResponseVo vo = future.get();
            List l1 =(List) vo.getItems();
            logger.info("item ->{}", l1);
        }



    }


    @Test
    public void testUpdateNonCheckInOrNonCheckOutOrNonUserId () {
        ksDormitoryService.updateNonCheckInOrNonCheckOutOrNonUserId();
    }

    @Test
    public void testUserDormitoryReport () {
        ksDormitoryService.userDormitoryReport(2018,4);
    }




}
