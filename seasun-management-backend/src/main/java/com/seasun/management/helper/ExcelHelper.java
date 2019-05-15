package com.seasun.management.helper;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.exception.DataImportException;
import com.seasun.management.mapper.FnTaskMapper;
import com.seasun.management.model.FnTask;
import com.seasun.management.util.MyBeanUtils;
import com.seasun.management.vo.ProjectVo;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelHelper {

    public static final String ERR_LOG_DIR = "devLog/";

    private static final Logger logger = LoggerFactory.getLogger(ExcelHelper.class);

    /**
     * 将文件写入到备份目录
     *
     * @param file
     * @param backupExcelUrl
     * @return
     * @throws IOException
     */
    public static String saveExcelBackup(MultipartFile file, String backupExcelUrl) throws IOException {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_hhmmss");
        String preFilename = format.format(date);
        String rand = String.valueOf(new Random().nextInt(1000));
        String fileName = preFilename + "_" + rand + "_" + file.getOriginalFilename();

        File backupFile = new File(backupExcelUrl + File.separator + fileName);
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(backupFile));
        outputStream.write(file.getBytes());
        outputStream.flush();
        outputStream.close();

        return backupFile.getPath();
    }

    public static String getYearAndMonth(Cell cell) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
        Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
        String dateString = sdf.format(date);
        dateString = dateString.replace("年", "-");
        dateString = dateString.replace("月", "-");
        return dateString;
    }

    /**
     * 保存到文件
     *
     * @param content
     * @param tag
     */
    public static void saveErrorFile(List<String> content, String subDir, String tag) {

        if (content.size() == 0) {
            return;
        }

        File rootDir = new File(ERR_LOG_DIR);
        if (!rootDir.exists()) {
            boolean result = rootDir.mkdir();
            if (!result)
                logger.info("文件创建失败");
        }

        String targetDir = ERR_LOG_DIR + subDir;
        File file = new File(targetDir);

        if (!file.exists()) {
            boolean result = file.mkdir();
            if (!result) logger.info("创建文件夹失败");
        }

        try (PrintWriter w = new PrintWriter(targetDir + tag + ".log")) {
            for (String line : content) {
                w.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> buildProjectUsedNames(ProjectVo projectVo) {
        List<String> projectUsedNames = new ArrayList<>();
        if (projectVo.getUsedNamesStr() != null) {
            String[] usedNames = projectVo.getUsedNamesStr().split(",");
            for (String usedName : usedNames) {
                usedName = ExcelHelper.trimSpaceAndSpecialSymbol(usedName);
                projectUsedNames.add(usedName);
            }
        }
        if (projectVo.getShortName() != null) {
            String shortName = ExcelHelper.trimSpaceAndSpecialSymbol(projectVo.getShortName());
            projectUsedNames.add(shortName);
        }
        if (projectVo.getName() != null) {
            String name = ExcelHelper.trimSpaceAndSpecialSymbol(projectVo.getName());
            projectUsedNames.add(name);
        }
        return projectUsedNames;
    }


    // 标准化字符,去除原来excel名称中的特殊符号,以及全角转半角,等.
    public static String trimSpaceAndSpecialSymbol(String destString) {
        destString = destString.replaceAll("[ |　| | ]", " ").trim()
                .replace("&", "").replace("+", "")
                .replace(":", "").replace("：", "")
                .replace("、", "").replace("/", "")
                .replace("~", "").replace("-", "").replace("——", "")
                .replace("(", "").replace("（", "")
                .replace(")", "").replace("）", "")
                .replace("【", "").replace("】", "");
        return destString;
    }


    /**
     * 清除目录下所有文件
     *
     * @param file
     */
    public static void deleteAll(File file) {

        String[] fileList;

        if (file.isFile() || ((fileList = file.list()) != null) && fileList.length == 0) {
            boolean result = file.delete();
            if (!result) logger.info("文件删除失败");
        } else {
            File[] files = file.listFiles();
            if (files == null)
                return;
            for (int i = 0; i < files.length; i++) {
                deleteAll(files[i]);
                boolean result = files[i].delete();
                if (!result) logger.info("删除文件失败");
            }
            if (file.exists()) {
                boolean result = file.delete();
                if (!result) logger.info("删除文件失败");
            }
        }
    }

    public static void checkTaskStatus(Long id) {
        FnTaskMapper fnTaskMapper = MyBeanUtils.getBean(FnTaskMapper.class);
        FnTask currentTask = fnTaskMapper.selectByPrimaryKey(id);
        if (currentTask.getStatus().equals(FnTask.TaskStatus.discard)) {
            logger.info("task has been discarded,will abort...");
            throw new DataImportException(ErrorCode.FILE_IMPORT_USER_CANCELED, "user force cancelled when processing...");
        }
    }

    public static class ExcelBuilder<T> {

        private Workbook wb;

        private Sheet sheet;

        private String path;

        public ExcelBuilder(String path) {
            this.wb = new HSSFWorkbook();
            this.sheet = wb.createSheet();
            this.path = path;
        }

        public ExcelBuilder init(List<List<String>> titles) {
            for (List<String> list : titles) {
                this.buildHead(list);
            }
            return buildSheetWidth(titles.get(0));
        }

        public ExcelBuilder buildSheetWidth(List<String> titles) {
            logger.info("titles -> {}", titles);
            if (titles == null) return this;
            for (Integer i = 0; i < titles.size(); i++) {
                sheet.setColumnWidth(i, "本月人才公寓第三间房数".getBytes().length * 256 + 300);
            }
            return this;
        }

        public Row buildRow() {
            return this.sheet.createRow(this.sheet.getLastRowNum() + 1);
        }

        /**
         * 采用反射, fields 为 object 对象的属性
         * */
        public ExcelBuilder buildBody (List<String> fields, List<T> objs) throws IllegalAccessException {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (T o : objs) {
                List<String> values = new ArrayList();
                    for (String f: fields) {
                        for (Class clazz = o.getClass(); clazz!=Object.class; clazz = clazz.getSuperclass()) {
                            Field field = null;
                            try {
                                field = clazz.getDeclaredField(f);
                                field.setAccessible(Boolean.TRUE);
                                String value = "";
                                if (!Objects.isNull(field.get(o))) {
                                    if (field.getType().getSimpleName().equals("Date")){
                                       Date d =  (Date) field.get(o);
                                       value = simpleDateFormat.format(d);
                                    }else {
                                        value = String.valueOf( field.get(o));
                                    }
                                }
                                values.add(value);
                            } catch (NoSuchFieldException e) {
                                //logger.error("导出excel error, o-> class {}, 没有对应的字段 {}", clazz, f);
                            }
                        }
                }
                buildRow(buildRow(), values, buiildCommonCellStyle());
            }

            return this;

        }

        public CellStyle buiildCommonCellStyle() {
            CellStyle cellStyle = wb.createCellStyle();
            Font font = wb.createFont();//#C0C0C0
            font.setColor(HSSFColor.BLACK.index);
            font.setFontHeightInPoints((short) 10);
            font.setFontName("微软雅黑");
            cellStyle.setFont(font);
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            return cellStyle;
        }

        public CellStyle buiildSpecialCellStyle() {
            CellStyle cellStyle = wb.createCellStyle();
            Font font = wb.createFont();//#C0C0C0
            font.setColor(HSSFColor.BLACK.index);
            font.setFontHeightInPoints((short) 10);
            font.setFontName("微软雅黑");
            cellStyle.setFont(font);
            cellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            return cellStyle;
        }


        public ExcelBuilder buildHead(List<String> titles) {
            CellStyle cellStyle = wb.createCellStyle();
            Font font = wb.createFont();//#C0C0C0
            font.setColor(HSSFColor.BLACK.index);
            font.setFontHeightInPoints((short) 12);
            font.setBold(true);
            font.setFontName("微软雅黑");
            cellStyle.setFont(font);
            cellStyle.setBorderBottom((short) 1);
            cellStyle.setBorderLeft((short) 1);
            cellStyle.setBorderRight((short) 1);
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            Row row = this.sheet.createRow(this.sheet.getLastRowNum() + 1);
            this.buildRow(row, titles, cellStyle);
            return this;
        }

        public void buildRow(Row row, List<String> titles, final CellStyle cellStyle) {
            for (Integer index = 0; index < titles.size(); index++) {
                Cell cell = row.createCell(index);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(titles.get(index));
            }
        }

        public void export() {
            OutputStream os = null;

            try {
                os = new FileOutputStream(this.path);
                this.wb.write(os);
            } catch (FileNotFoundException e) {
                logger.error("文件无法找到 -> {}", e.getMessage());
            } catch (IOException e) {
                logger.error("IO 异常 -> {}", e.getMessage());
            } finally {
                try {
                    if (os != null) os.close();
                    if (wb != null) wb.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
