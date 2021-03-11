package org.example;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author whoami
 */
public class App 
{
    private static final Map<String, Map<String,List<Sit>>> sit_Module=new HashMap<>();
    private static final Map<String, Map<String,List<Sit>>> sit_Type=new HashMap<>();
    private static String today ;
    private static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");
    private static  XSSFWorkbook xssfWorkbook ;
    private String file_Path;
    private static final String new_Sheet_Name="自动统计";
    public void App( String path ) throws IOException {
        file_Path=path;
        if (file_Path == null) {
            return;
        }
        System.out.println(file_Path);

        Date date = new Date();

        today =  FORMAT_DATE.format(date);
        File file = new File(file_Path);
        FileInputStream fileInputStream = new FileInputStream(file);
        xssfWorkbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet sheet = xssfWorkbook.getSheet("SIT测试问题清单");
        getData(sheet);

        XSSFSheet sitSheet = xssfWorkbook.getSheet("sit测试问题清单（回归测试）");
        getData(sitSheet);
        int sheetIndex = xssfWorkbook.getSheetIndex(new_Sheet_Name);
        if(sheetIndex != -1){
            xssfWorkbook.removeSheetAt(sheetIndex);
        }
        XSSFSheet autoSheet = xssfWorkbook.createSheet(new_Sheet_Name);
        automatic(autoSheet,sit_Module);
        automatic(autoSheet,sit_Type);

        JOptionPane.showMessageDialog(null,"完成");
    }

    public void getData(XSSFSheet sheet ){
        int lastRowNum = sheet.getLastRowNum();

        for (int i =2; i < lastRowNum ; i++) {
            XSSFRow row = sheet.getRow(i);
            String type = row.getCell(2).getStringCellValue();
            String module = row.getCell(3).getStringCellValue();
            String status = row.getCell(10).getStringCellValue();
            double numericCellValue = row.getCell(7).getNumericCellValue();
            if (type == null || "".equals(type)) {
                break;
            }
            Sit sit = new Sit(type, module, status, dateTransform(numericCellValue));
            if(sit_Module.containsKey(sit.getModule())){ {
                generate(sit, sit_Module,sit.getModule());
            }
            } else if(!sit_Module.containsKey(sit.getModule())){
                ArrayList<Sit> list = new ArrayList<>();
                list.add(sit);
                Map<String, List<Sit>> hash = new HashMap<>(0);
                hash.put(sit.getStatus(), list);
                sit_Module.put(sit.getModule(), hash);
            }

            if (sit_Type.containsKey(sit.getType())) {
                generate(sit, sit_Type, sit.getType());
            } else if (!sit_Type.containsKey(sit.getType())) {
                ArrayList<Sit> list = new ArrayList<>();
                list.add(sit);
                Map<String, List<Sit>> hash = new HashMap<>(0);
                hash.put(sit.getStatus(), list);
                sit_Type.put(sit.getType(), hash);
            }
        }
    }


    public  void generate(Sit sit,Map<String, Map<String,List<Sit>>> sitMap,String value){
        Map<String, List<Sit>> stringListMap = sitMap.get(value);
        if(stringListMap.containsKey(sit.getStatus())){
            stringListMap.get(sit.getStatus()).add(sit);
        } else if (!stringListMap.containsKey(sit.getStatus())) {
            List<Sit> sits = new ArrayList<>();
            sits.add(sit);
            stringListMap.put(sit.getStatus(), sits);
        }
    }

    public String dateTransform(double time) {
        Date date = DateUtil.getJavaDate(time);
        return FORMAT_DATE.format(date);
    }

    public void automatic(XSSFSheet autoSheet,Map<String, Map<String,List<Sit>>> sitMap){

        for (Map.Entry<String, Map<String,List<Sit>>> entry : sitMap.entrySet()) {
            int deal_problem_num = 0, make_problem_num = 0, cancel_problem_num = 0, today_problem_num = 0;
            for (Map.Entry<String, List<Sit>> entity : entry.getValue().entrySet()) {
                for (Sit temp_sit : entity.getValue()) {
                    if (today.equals(temp_sit.getDate())) {
                        today_problem_num++;
                    }
                }
                switch (entity.getKey()) {
                    case "已处理":
                    case "已确认":
                        deal_problem_num = deal_problem_num + entity.getValue().size();
                        break;
                    case "进行中":
                    case "未开始":
                        make_problem_num = make_problem_num + entity.getValue().size();
                        break;
                    case "取消":
                        deal_problem_num = deal_problem_num + entity.getValue().size();
                        cancel_problem_num = cancel_problem_num + entity.getValue().size();
                    default:
                        break;
                }
            }
            System.out.println(entry.getKey() + "/" + deal_problem_num + "/" + make_problem_num + "/" + cancel_problem_num + "/" + today_problem_num);
            printData(autoSheet,entry.getKey(), deal_problem_num, make_problem_num, cancel_problem_num, today_problem_num);
        }
    }

    public  void printData(XSSFSheet sheet,String title, int A, int B, int C, int D) {
        int lastRowNum = sheet.getLastRowNum();
        if(lastRowNum == -1 )
        {
            lastRowNum = 0;
        }
        else{
            lastRowNum++;
        }
        XSSFRow row = sheet.createRow(lastRowNum);
        XSSFCell cell = row.createCell(0);
        XSSFCell aNum = row.createCell(1);
        XSSFCell bNum = row.createCell(2);
        XSSFCell cNum = row.createCell(3);
        XSSFCell dNum = row.createCell(4);
        cell.setCellValue(title);
        aNum.setCellValue(A);bNum.setCellValue(B);cNum.setCellValue(C);dNum.setCellValue(D);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file_Path);
            xssfWorkbook.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
