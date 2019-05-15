package com.seasun.management.util;


import com.seasun.management.model.PmAttachment;
import com.seasun.management.vo.BaseFileVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io

        .*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MyFileUtils {

    private static final Logger logger = LoggerFactory.getLogger(MyFileUtils.class);

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        InputStream inStream = null;
        FileOutputStream fs = null;

        int bytesum = 0;
        int byteread = 0;
        File oldfile = new File(oldPath);
        if (oldfile.exists()) { //文件存在时
            try {
                inStream = new FileInputStream(oldPath); //读入原文件
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
            } catch (IOException e) {
                System.out.println("复制单个文件操作出错");

            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        System.out.println("关闭读入文件流失败");
                        e.printStackTrace();
                    }
                }
                if (fs != null) {
                    try {
                        fs.close();
                    } catch (IOException e) {
                        System.out.println("关闭输出文件流失败");
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    private static void deleteDir(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteDir(files[i]);
            }
        }
        dir.delete();
    }

    /**
     * 创建目录
     *
     * @param isDelete 是否删除再重新建立
     * @param filePath 文件路径
     * @return
     */
    public static boolean makeDirs(boolean isDelete, String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        File folder = new File(filePath);
        if (isDelete && folder.exists()) {
            deleteDir(folder);
        }
        return folder.exists() ? false : folder.mkdirs();
    }

    /**
     * 文件拷贝
     *
     * @param srcPath 原目录
     * @param desPath 新目录
     * @throws IOException
     */
    public static void copyFileUsingFileChannels(String srcPath, String desPath) {
        File source = new File(srcPath);
        if (source != null && source.exists()) {
            File dest = new File(desPath);
            FileChannel inputChannel = null;
            FileChannel outputChannel = null;
            try {
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputChannel != null) {
                        inputChannel.close();
                    }
                    if (outputChannel != null) {
                        outputChannel.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // todo : 只支持文本文件 txt,sql,json...，换行符，空格符要写在String里面
    public static boolean outputListToFile(List<String> list, String filePath) {
        boolean okFlag = true;
        if (list.isEmpty()) {
            return false;
        }

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(filePath);
            for (String o : list) {
                if (o == null) {
                    continue;
                }
                pw.write(o);
            }

        } catch (Exception e) {
            okFlag = false;
            logger.error(e.getMessage());
        } finally {
            if (pw != null) {
                pw.flush();
                pw.close();
            }
        }
        return okFlag;
    }
}