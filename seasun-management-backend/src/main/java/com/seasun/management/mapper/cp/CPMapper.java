package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.CP;
import com.seasun.management.vo.cp.OutsourcerVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CPMapper {
    @Delete({
        "delete from cp.cp",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into cp.cp (Name, FullName, ",
        "StartDate, City, ",
        "Receipt, Certificate, NameIsSame, ",
        "Contacts, QQ, Phone, ",
        "Mail, Boss, Channel, ",
        "GoodStyle, Entry, ",
        "Sign, ContractNo, ",
        "Suggest, Employees, ",
        "Grade, Description, ",
        "Address, WebSite, ",
        "Direction, FileLastWriteTime, ",
        "ScheduleLastWriteTime, CreatedTime, ",
        "Status, ContactMan, ",
        "ContactPhone, LegalRepresentative, ",
        "Bank, Account, AccountName, ",
        "Personal, Exp)",
        "values (#{name,jdbcType=VARCHAR}, #{fullName,jdbcType=VARCHAR}, ",
        "#{startDate,jdbcType=VARCHAR}, #{city,jdbcType=VARCHAR}, ",
        "#{receipt,jdbcType=BIT}, #{certificate,jdbcType=BIT}, #{nameIsSame,jdbcType=BIT}, ",
        "#{contacts,jdbcType=VARCHAR}, #{qq,jdbcType=VARCHAR}, #{phone,jdbcType=VARCHAR}, ",
        "#{mail,jdbcType=VARCHAR}, #{boss,jdbcType=VARCHAR}, #{channel,jdbcType=VARCHAR}, ",
        "#{goodStyle,jdbcType=VARCHAR}, #{entry,jdbcType=VARCHAR}, ",
        "#{sign,jdbcType=VARCHAR}, #{contractNo,jdbcType=VARCHAR}, ",
        "#{suggest,jdbcType=VARCHAR}, #{employees,jdbcType=INTEGER}, ",
        "#{grade,jdbcType=INTEGER}, #{description,jdbcType=VARCHAR}, ",
        "#{address,jdbcType=VARCHAR}, #{webSite,jdbcType=VARCHAR}, ",
        "#{direction,jdbcType=VARCHAR}, #{fileLastWriteTime,jdbcType=VARCHAR}, ",
        "#{scheduleLastWriteTime,jdbcType=VARCHAR}, #{createdTime,jdbcType=VARCHAR}, ",
        "#{status,jdbcType=INTEGER}, #{contactMan,jdbcType=VARCHAR}, ",
        "#{contactPhone,jdbcType=VARCHAR}, #{legalRepresentative,jdbcType=VARCHAR}, ",
        "#{bank,jdbcType=VARCHAR}, #{account,jdbcType=VARCHAR}, #{accountName,jdbcType=VARCHAR}, ",
        "#{personal,jdbcType=TINYINT}, #{exp,jdbcType=LONGVARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(CP record);

    int insertSelective(CP record);

    @Select({
        "select",
        "ID, Name, FullName, StartDate, City, Receipt, Certificate, NameIsSame, Contacts, ",
        "QQ, Phone, Mail, Boss, Channel, GoodStyle, Entry, Sign, ContractNo, Suggest, ",
        "Employees, Grade, Description, Address, WebSite, Direction, FileLastWriteTime, ",
        "ScheduleLastWriteTime, CreatedTime, Status, ContactMan, ContactPhone, LegalRepresentative, ",
        "Bank, Account, AccountName, Personal, Exp",
        "from cp.cp",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.cp.CPMapper.ResultMapWithBLOBs")
    CP selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CP record);

    @Update({
        "update cp.cp",
        "set Name = #{name,jdbcType=VARCHAR},",
          "FullName = #{fullName,jdbcType=VARCHAR},",
          "StartDate = #{startDate,jdbcType=VARCHAR},",
          "City = #{city,jdbcType=VARCHAR},",
          "Receipt = #{receipt,jdbcType=BIT},",
          "Certificate = #{certificate,jdbcType=BIT},",
          "NameIsSame = #{nameIsSame,jdbcType=BIT},",
          "Contacts = #{contacts,jdbcType=VARCHAR},",
          "QQ = #{qq,jdbcType=VARCHAR},",
          "Phone = #{phone,jdbcType=VARCHAR},",
          "Mail = #{mail,jdbcType=VARCHAR},",
          "Boss = #{boss,jdbcType=VARCHAR},",
          "Channel = #{channel,jdbcType=VARCHAR},",
          "GoodStyle = #{goodStyle,jdbcType=VARCHAR},",
          "Entry = #{entry,jdbcType=VARCHAR},",
          "Sign = #{sign,jdbcType=VARCHAR},",
          "ContractNo = #{contractNo,jdbcType=VARCHAR},",
          "Suggest = #{suggest,jdbcType=VARCHAR},",
          "Employees = #{employees,jdbcType=INTEGER},",
          "Grade = #{grade,jdbcType=INTEGER},",
          "Description = #{description,jdbcType=VARCHAR},",
          "Address = #{address,jdbcType=VARCHAR},",
          "WebSite = #{webSite,jdbcType=VARCHAR},",
          "Direction = #{direction,jdbcType=VARCHAR},",
          "FileLastWriteTime = #{fileLastWriteTime,jdbcType=VARCHAR},",
          "ScheduleLastWriteTime = #{scheduleLastWriteTime,jdbcType=VARCHAR},",
          "CreatedTime = #{createdTime,jdbcType=VARCHAR},",
          "Status = #{status,jdbcType=INTEGER},",
          "ContactMan = #{contactMan,jdbcType=VARCHAR},",
          "ContactPhone = #{contactPhone,jdbcType=VARCHAR},",
          "LegalRepresentative = #{legalRepresentative,jdbcType=VARCHAR},",
          "Bank = #{bank,jdbcType=VARCHAR},",
          "Account = #{account,jdbcType=VARCHAR},",
          "AccountName = #{accountName,jdbcType=VARCHAR},",
          "Personal = #{personal,jdbcType=TINYINT},",
          "Exp = #{exp,jdbcType=LONGVARCHAR}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKeyWithBLOBs(CP record);

    @Update({
        "update cp.cp",
        "set Name = #{name,jdbcType=VARCHAR},",
          "FullName = #{fullName,jdbcType=VARCHAR},",
          "StartDate = #{startDate,jdbcType=VARCHAR},",
          "City = #{city,jdbcType=VARCHAR},",
          "Receipt = #{receipt,jdbcType=BIT},",
          "Certificate = #{certificate,jdbcType=BIT},",
          "NameIsSame = #{nameIsSame,jdbcType=BIT},",
          "Contacts = #{contacts,jdbcType=VARCHAR},",
          "QQ = #{qq,jdbcType=VARCHAR},",
          "Phone = #{phone,jdbcType=VARCHAR},",
          "Mail = #{mail,jdbcType=VARCHAR},",
          "Boss = #{boss,jdbcType=VARCHAR},",
          "Channel = #{channel,jdbcType=VARCHAR},",
          "GoodStyle = #{goodStyle,jdbcType=VARCHAR},",
          "Entry = #{entry,jdbcType=VARCHAR},",
          "Sign = #{sign,jdbcType=VARCHAR},",
          "ContractNo = #{contractNo,jdbcType=VARCHAR},",
          "Suggest = #{suggest,jdbcType=VARCHAR},",
          "Employees = #{employees,jdbcType=INTEGER},",
          "Grade = #{grade,jdbcType=INTEGER},",
          "Description = #{description,jdbcType=VARCHAR},",
          "Address = #{address,jdbcType=VARCHAR},",
          "WebSite = #{webSite,jdbcType=VARCHAR},",
          "Direction = #{direction,jdbcType=VARCHAR},",
          "FileLastWriteTime = #{fileLastWriteTime,jdbcType=VARCHAR},",
          "ScheduleLastWriteTime = #{scheduleLastWriteTime,jdbcType=VARCHAR},",
          "CreatedTime = #{createdTime,jdbcType=VARCHAR},",
          "Status = #{status,jdbcType=INTEGER},",
          "ContactMan = #{contactMan,jdbcType=VARCHAR},",
          "ContactPhone = #{contactPhone,jdbcType=VARCHAR},",
          "LegalRepresentative = #{legalRepresentative,jdbcType=VARCHAR},",
          "Bank = #{bank,jdbcType=VARCHAR},",
          "Account = #{account,jdbcType=VARCHAR},",
          "AccountName = #{accountName,jdbcType=VARCHAR},",
          "Personal = #{personal,jdbcType=TINYINT}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(CP record);

    /* the flowing are user defined ... */

    List<OutsourcerVo> selectByCond(@Param("year") Integer year, @Param("grades")  List<Integer> grades, @Param("makingType") String makingType, @Param("styleType") String styleType,@Param("sortField") String sortField);

    Integer selectCountByCond(@Param("year") Integer year,@Param("grades")  List<Integer> grades, @Param("makingType") String makingType,@Param("styleType") String styleType);
}