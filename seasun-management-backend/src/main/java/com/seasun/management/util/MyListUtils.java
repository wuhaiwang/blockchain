package com.seasun.management.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MyListUtils {

    // 数组去重
    public static List removeDuplicate(List inputList) {
        HashSet h = new HashSet(inputList);
        inputList.clear();
        inputList.addAll(h);
        List list = inputList;
        return list;
    }

    /**
     * 深拷贝对象，注意 obj需要实现 Serializable 接口
     *
     * @param obj
     * @return
     * @throws IOException
     * @throws OptionalDataException
     * @throws ClassNotFoundException
     */
    public static Object deepClone(Object obj) throws IOException, OptionalDataException, ClassNotFoundException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(obj);
        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);
        return oi.readObject();
    }

    //  数组截取，可用于分页
    public static List paging(List list, int currentPage, int pageSize) {
        List result = null;
        int size = list.size();
        if (size >= (currentPage + 1) * pageSize) {
            result = list.subList(currentPage * pageSize, (currentPage + 1) * pageSize);
        } else if (size > currentPage * pageSize) {
            result = list.subList(currentPage * pageSize, size);
        }

        if (result == null) {
            result = new ArrayList<>(1);
        }
        return result;
    }
}
