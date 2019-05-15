package com.seasun.management.service.kingsoftLife;

import com.alibaba.fastjson.JSON;
import com.seasun.management.common.KsHttpComponent;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.exception.KsException;
import com.seasun.management.model.KsConference;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.util.URLUtil;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.seasun.management.constant.ErrorCode.USER_INVALID_OPERATE_ERROR;

@Service
public class KsConferenceImpl implements KsConferenceService {


    private Logger logger = LoggerFactory.getLogger(KsConferenceImpl.class);

    @Value("${ks.life.address}")
    private String address;

    private static final String beginTime = "08:00";
    private static final String endTime = "23:00";

    @Override
    public CommonResponse getAllConferences(String date, int floor, int building) {
        List<ConferenceVo> conferenceVoList = new ArrayList<>(60);

        String loginId = MyTokenUtils.getCurrentUser().getLoginId();

        String location = String.valueOf(building) + "#-" + floor + "F";
        HashMap<String, String> param = new HashMap<String, String>() {{
            put("location", URLUtil.encodeURIComponent(location));
        }};

        List<KsConferenceVo.Room> allRoom = getAllRoomList(loginId, param).getModelSet();

        for (KsConferenceVo.Room room : allRoom) {
            List<KsReserveConferenceVo.ReserveConference> ksReserveConferences = getReserveRoomDetailByRoomId(room.getId(), date, loginId);
            if (ksReserveConferences == null || ksReserveConferences.size() == 0) { //全天可用
                conferenceVoList.add(createFullDayRecordByRoom(room, date));
            } else {// 已经有预定记录
                conferenceVoList.add(convertToConference(ksReserveConferences));
            }
        }

        // 前台只显示可以预定时间段
        // ugly but 业务逻辑需要
        Date nowTime = new Date();
        for (ConferenceVo conferenceVo : conferenceVoList) {
            List<ConferenceVo.ConferenceRange> conferenceRangeList = new ArrayList<>();
            for (int i = 0; i < conferenceVo.getConferenceRanges().size() && conferenceRangeList.size() < 3; i++) {

                if (conferenceVo.getConferenceRanges().get(i).getCanReserveFlag()) {

                    if (isNowTime(date, nowTime)) {
                        if (compareTime(conferenceVo.getConferenceRanges().get(i).getToTime(), nowTime)) {
                            if (!compareTime(conferenceVo.getConferenceRanges().get(i).getFromTime(), nowTime)) {
                                conferenceVo.getConferenceRanges().get(i).setFromTime(getNext15BeginTime(nowTime));
                            }
                            conferenceRangeList.add(conferenceVo.getConferenceRanges().get(i));
                        }
                    } else {
                        conferenceRangeList.add(conferenceVo.getConferenceRanges().get(i));
                    }
                }
            }
            conferenceVo.setConferenceRanges(conferenceRangeList);
        }
        return new CommonResponse(0, "success", conferenceVoList);
    }


    private boolean compareTime(String toTime, Date compareTime) {
        String formatDate = add15formatDate(compareTime);
        String nowTime = new String(formatDate.getBytes(), 11, 5);
        return toTime.compareTo(nowTime) > 0;
    }

    private boolean isNowTime(String dateStr, Date nowTime) {
        String formatDate = add15formatDate(nowTime);
        String nowDay = new String(formatDate.getBytes(), 0, 10);
        return nowDay.contains(dateStr);
    }

    private String add15formatDate(Date date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, 15);
        return format.format(calendar.getTime());
    }

    private String getNext15BeginTime(Date nowTimeDate) {
        String resultTime = "";
        DateFormat format = new SimpleDateFormat("mm", Locale.CHINA);
        String nowTime = format.format(nowTimeDate);

        DateFormat formatHH = new SimpleDateFormat("HH", Locale.CHINA);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowTimeDate);

        String[] all15Times = {"00", "15", "30", "45", "00"};

        for (int i = 0; i < all15Times.length - 1; i++) {
            if (nowTime.compareTo(all15Times[i]) == 0) {

                String hour = formatHH.format(nowTimeDate);
                return hour + ":" + nowTime;

            } else if (nowTime.compareTo(all15Times[i]) > 0 && (nowTime.compareTo(all15Times[i + 1]) < 0 || i == all15Times.length - 2)) {

                if (i == all15Times.length - 2) {
                    calendar.add(Calendar.HOUR_OF_DAY, 1);
                }
                Date afterTime = calendar.getTime();

                String hour = formatHH.format(afterTime);
                return hour + ":" + all15Times[i + 1];
            }
        }
        return resultTime;
    }


    @Override
    public CommonResponse searchConference(String location, String common) {
        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        HashMap<String, String> param = new HashMap<>();
        param.put("location", location);
        param.put("common", common);
        KsConferenceVo ksConference = getAllRoomList(loginId, param);
        return new CommonResponse(0, "success", ksConference);
    }

    @Override
    public CommonResponse reserveConference(KsConference ksConference) {
        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        String reserveUrl = "/meetingRoomBooking/add";
        KsHttpComponent component = new KsHttpComponent();
        String json = JSON.toJSONString(ksConference);
        String response = component.doPostHttpRequest(address + reserveUrl, json, loginId);
        KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);

        if (Boolean.parseBoolean(ksResult.getSuccess())) {
            String decryptedData = (String) component.ksDecryptData(ksResult.getData().toString());
            return new CommonResponse(0, "success", JSON.parseObject(decryptedData, JSON.class));
        } else {
            return new CommonResponse(ErrorCode.USER_INVALID_OPERATE_ERROR, ksResult.getMsg());
        }
    }

    @Override
    public CommonResponse getConferenceDetail(long roomId, String date) {

        ConferenceVo conferenceVo = new ConferenceVo();

        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        List<KsReserveConferenceVo.ReserveConference> reserveConferences = getReserveRoomDetailByRoomId(roomId, date, loginId);

        if (reserveConferences == null || reserveConferences.size() == 0) { //全天可用
            createFullDay15TimeRange(roomId, conferenceVo, date);
        } else {// 已经有预定记录
            conferenceVo = convertToConference(reserveConferences);
            conferenceVo.setConferenceRanges(createNotFullDay15TimeRange(date, conferenceVo));
        }
        return new CommonResponse(0, "success", conferenceVo);
    }

    @Override
    public CommonResponse conferenceDetail(long id) {
        KsHttpComponent component = new KsHttpComponent();
        KsRoomVo.KsRoom room = getConferenceDetail(id, component);
        return new CommonResponse(0, "success", room);
    }

    @Override
    public CommonResponse cancelConference(long id) {
        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        String cancelUrl = "/meetingRoomBooking/cancel?id=" + id;
        KsHttpComponent component = new KsHttpComponent();
        String response = component.doPostHttpRequest(address + cancelUrl, null, loginId);

        KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);
        if (Boolean.parseBoolean(ksResult.getSuccess())) {
            return new CommonResponse(0, "success");
        } else {
            return new CommonResponse(ErrorCode.USER_INVALID_OPERATE_ERROR, ksResult.getMsg());
        }
    }

    @Override
    public CommonResponse myConference(String date, int pageNo, int pageSize) {
        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        String myResUrl = "/meetingRoomBooking/listMy";
        Map<String, String> param = new HashMap<String, String>() {{
            put("date", date);
            put("pageNo", String.valueOf(pageNo));
            put("pageSize", String.valueOf(pageSize));
            put("sort", "start_time");
            put("order", "desc");
        }};
        KsHttpComponent component = new KsHttpComponent();
        String response = component.doGetHttpRequest(address + myResUrl, param, loginId,null);

        KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);
        if (Boolean.parseBoolean(ksResult.getSuccess())) {
            String decryptedData = (String) component.ksDecryptData(ksResult.getData().toString());
            return new CommonResponse(0, "success", JSON.parseObject(decryptedData, KsReserveConferenceVo.class));
        } else {
            return new CommonResponse(ErrorCode.USER_INVALID_OPERATE_ERROR, ksResult.getMsg());
        }
    }


    private KsConferenceVo getAllRoomList(String loginId, HashMap<String, String> param) {

        KsConferenceVo result = new KsConferenceVo();
        List<KsConferenceVo.Room> rooms = new ArrayList<>();
        result.setModelSet(rooms);

        String searchUrl = "/meetingRoom/search";
        KsHttpComponent component = new KsHttpComponent();
        int pageNo = 1;
        int pageSize = 50;
        while (true) {
            String response = component.doGetHttpRequest(address + searchUrl, param, loginId,null);
            KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);
            if (!Boolean.parseBoolean(ksResult.getSuccess())) {
                throw new KsException(USER_INVALID_OPERATE_ERROR, "KS 接口调用失败");
            }

            String decryptedData = (String) component.ksDecryptData(ksResult.getData().toString());
            KsConferenceVo ksConferenceVo = JSON.parseObject(decryptedData, KsConferenceVo.class);

            result.setTotal(ksConferenceVo.getTotal());
            result.getModelSet().addAll(ksConferenceVo.getModelSet());

            // 一次请求获得了所有的数据
            if (ksConferenceVo.getTotal() <= result.getModelSet().size()) {
                break;
            }

            // 添加页数，继续请求
            pageNo++;
            if (param == null) {
                param = new HashMap<>();
            }

            param.put("pageNo", String.valueOf(pageNo));
            param.put("pageSize", String.valueOf(pageSize));
        }
        return result;
    }

    private List<KsReserveConferenceVo.ReserveConference> getReserveRoomDetailByRoomId(Long roomId, String date, String loginId) {
        String roomListUrl = "/meetingRoomBooking/list";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("date", date);
        paramMap.put("roomId", String.valueOf(roomId));
        KsHttpComponent component = new KsHttpComponent();
        String response = component.doGetHttpRequest(address + roomListUrl, paramMap, loginId,null);
        KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);
        String decryptedData = (String) component.ksDecryptData(ksResult.getData().toString());
        KsReserveConferenceVo ksReserveConferenceVo = JSON.parseObject(decryptedData, KsReserveConferenceVo.class);
        return ksReserveConferenceVo.getModelSet();
    }

    private ConferenceVo convertToConference(List<KsReserveConferenceVo.ReserveConference> ksReserveConferences) {

        ConferenceVo conferenceVo = new ConferenceVo();

        // 填充 id， roomId ，roomName 等公共信息
        BeanUtils.copyProperties(ksReserveConferences.get(0), conferenceVo);
        conferenceVo.setLocation(ksReserveConferences.get(0).getRoomLocation());
        conferenceVo.setId(ksReserveConferences.get(0).getRoomId());

        // 填充时间段信息
        List<ConferenceVo.ConferenceRange> conferenceRanges = new ArrayList<>();
        String tempBeginTime = beginTime;

        for (KsReserveConferenceVo.ReserveConference conference : ksReserveConferences) {
            if ((conference.getBookingState().equals("BOOK_SUCCESS") || conference.getBookingState().equals("APPROVING")) && tempBeginTime.compareTo(endTime) < 0) {
                if (!tempBeginTime.equals(conference.getFromTime())) { // tempTime 与 fromTime 不一致，说明有空闲时间段
                    addUnReservedConference(conferenceRanges, tempBeginTime, conference.getFromTime());
                }
                addReservedConference(conferenceRanges, conference);
                tempBeginTime = conference.getToTime();
            }
        }

        if (!tempBeginTime.equals(endTime)) {
            addUnReservedConference(conferenceRanges, tempBeginTime, endTime);
        }
        conferenceVo.setConferenceRanges(conferenceRanges);
        return conferenceVo;
    }

    private void addReservedConference(List<ConferenceVo.ConferenceRange> conferenceRanges, KsReserveConferenceVo.ReserveConference sourceConference) {
        ConferenceVo.ConferenceRange rangeReserve = new ConferenceVo.ConferenceRange();
        BeanUtils.copyProperties(sourceConference, rangeReserve);
        rangeReserve.setCanReserveFlag(false);
        conferenceRanges.add(rangeReserve);
    }

    private void addUnReservedConference(List<ConferenceVo.ConferenceRange> conferenceRanges, String beginTime, String endTime) {
        ConferenceVo.ConferenceRange rangeUnRes = new ConferenceVo.ConferenceRange();
        rangeUnRes.setFromTime(beginTime);
        rangeUnRes.setToTime(endTime);
        rangeUnRes.setCanReserveFlag(true);
        conferenceRanges.add(rangeUnRes);
    }

    private KsRoomVo.KsRoom getConferenceDetail(long id, KsHttpComponent component) {
        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        String detailUrl = "/meetingRoomBooking/detail?id=" + id;
        String response = component.doGetHttpRequest(address + detailUrl, null, loginId,null);
        KsLifeCommonVo conferenceDetail = KsHttpComponent.responseToObj(response);

        if (!Boolean.parseBoolean(conferenceDetail.getSuccess())) {
            throw new KsException(USER_INVALID_OPERATE_ERROR, "KS 接口调用失败");
        }
        String decryptedData = (String) component.ksDecryptData(conferenceDetail.getData().toString());
        return JSON.parseObject(decryptedData, KsRoomVo.class).getModel();
    }

    private void createFullDay15TimeRange(Long roomId, ConferenceVo conferenceVo, String dateStr) {
        KsHttpComponent component = new KsHttpComponent();
        KsRoomVo.KsRoom ksRoom = getConferenceDetail(roomId, component);
        conferenceVo.setId(ksRoom.getId());
        conferenceVo.setRoomName(ksRoom.getRoomName());
        conferenceVo.setLocation(ksRoom.getRoomLocation());

        String beginTimeDay = dateStr + " " + beginTime;
        String endTimeDay = dateStr + " " + endTime;

        conferenceVo.setConferenceRanges(split15TimeRange(beginTimeDay, endTimeDay));
    }

    private List<ConferenceVo.ConferenceRange> split15TimeRange(String beginTimeDay, String endTimeDay) {

        List<ConferenceVo.ConferenceRange> result = new ArrayList<>();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        Date dateBegin = null;
        Date dateEnd = null;
        try {
            dateBegin = format.parse(beginTimeDay);
            dateEnd = format.parse(endTimeDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        while (dateBegin.getTime() < dateEnd.getTime()) {
            ConferenceVo.ConferenceRange conferenceRange = new ConferenceVo.ConferenceRange();
            conferenceRange.setFromTime(format.format(dateBegin));

            Calendar ca = Calendar.getInstance();
            ca.setTime(dateBegin);
            ca.add(Calendar.MINUTE, 15);
            dateBegin = ca.getTime();
            conferenceRange.setToTime(format.format(dateBegin));
            conferenceRange.setCanReserveFlag(true);
            result.add(conferenceRange);
        }
        return result;
    }

    private List<ConferenceVo.ConferenceRange> createNotFullDay15TimeRange(String dateStr, ConferenceVo conferenceVo) {
        List<ConferenceVo.ConferenceRange> originList = conferenceVo.getConferenceRanges();
        List<ConferenceVo.ConferenceRange> result = new ArrayList<>();

        for (ConferenceVo.ConferenceRange origin : originList) {
            String beginTimeDay = dateStr + " " + origin.getFromTime();
            String endTimeDay = dateStr + " " + origin.getToTime();
            List<ConferenceVo.ConferenceRange> conferenceRanges;

            conferenceRanges = split15TimeRange(beginTimeDay, endTimeDay);

            if (!origin.getCanReserveFlag()) {
                for (ConferenceVo.ConferenceRange range : conferenceRanges) {
                    range.setLevel(origin.getLevel());
                    range.setState(origin.getState());
                    range.setUserName(origin.getUserName());
                    range.setCanReserveFlag(false);
                }
            }
            result.addAll(conferenceRanges);
        }
        return result;
    }

    private ConferenceVo createFullDayRecordByRoom(KsConferenceVo.Room room, String date) {
        ConferenceVo conferenceVo = new ConferenceVo();
        List<ConferenceVo.ConferenceRange> ranges = new ArrayList<>();
        BeanUtils.copyProperties(room, conferenceVo);
        Date nowDate = new Date();
        String nowDay = new String(add15formatDate(nowDate).getBytes(), 0, 10);
        ConferenceVo.ConferenceRange conferenceRange;
        if (!date.equals(nowDay)) {
            conferenceRange = new ConferenceVo.ConferenceRange(true, beginTime, endTime);
        } else {
            conferenceRange = new ConferenceVo.ConferenceRange(true, getNext15BeginTime(nowDate), endTime);
        }
        ranges.add(conferenceRange);
        conferenceVo.setConferenceRanges(ranges);
        return conferenceVo;
    }

}
