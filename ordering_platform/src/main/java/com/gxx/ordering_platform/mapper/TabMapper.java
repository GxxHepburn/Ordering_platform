package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.Multi_Tabtype_Tab;
import com.gxx.ordering_platform.entity.Tab;

public interface TabMapper {

	@Select("SELECT * FROM tab WHERE T_ID = #{t_id}")
	Tab getByTabId(@Param("t_id") int t_id);
	
	@Select("SELECT * FROM tabtype, tab WHERE TT_ID = T_TTID AND T_MID = #{t_mid} AND T_Name like concat(#{query}, '%') limit #{limitStart}, #{pagesize}")
	List<Multi_Tabtype_Tab> getByMID(@Param("t_mid") int t_mid, @Param("limitStart") int limitStart, @Param("pagesize") int pagesize, @Param("query") String query);
	
	@Select("SELECT * FROM tabtype, tab WHERE TT_ID = T_TTID AND T_MID = #{t_mid} AND T_Name like concat(#{query}, '%') AND T_TTID = #{t_ttid} limit #{limitStart}, #{pagesize}")
	List<Multi_Tabtype_Tab> getByMIDANDTTID(@Param("t_mid") int t_mid, @Param("limitStart") int limitStart, @Param("pagesize") int pagesize, @Param("query") String query, @Param("t_ttid") int t_ttid);

	
	@Select("SELECT COUNT(*) FROM tab WHERE T_MID = #{t_mid} AND T_Name like concat(#{query}, '%')")
	int getTotalByMid(@Param("t_mid") int t_mid, @Param("query") String query);
	
	@Select("SELECT COUNT(*) FROM tab WHERE T_MID = #{t_mid} AND T_Name like concat(#{query}, '%') AND T_TTID = #{t_ttid}")
	int getTotalByMidANDTTID(@Param("t_mid") int t_mid, @Param("query") String query, @Param("t_ttid") int t_ttid);
	
	@Delete("DELETE FROM tab WHERE T_ID = #{t_id}")
	void deleteByTID(@Param("t_id") int t_id);
	
	@Update("UPDATE tab SET T_TTID = #{t_ttid}, T_Name = #{t_name}, T_PeopleOfDiners = #{t_peopleOfDiners} WHERE T_ID = #{t_id}")
	void updateByTID(@Param("t_id") int t_id, @Param("t_ttid") int t_ttid, @Param("t_name") String t_name, @Param("t_peopleOfDiners") int t_peopleOfDiners);
	
	@Insert("INSERT INTO tab (T_MID, T_TTID, T_Name, T_PeopleOfDiners) VALUES (#{t_mid}, #{t_ttid}, #{t_name}, #{t_peopleOfDiners})")
	void insert(@Param("t_mid") int t_mid, @Param("t_ttid") int t_ttid, @Param("t_name") String t_name, @Param("t_peopleOfDiners") int t_peopleOfDiners);
	
	@Select("SELECT * FROM tab WHERE T_TTID = #{t_ttid} LIMIT 1")
	Tab isTabTypeNullByTTID(@Param("t_ttid") int t_ttid);
	
	@Delete("DELETE FROM tab WHERE T_TTID = #{t_ttid}")
	void deleteByTTID(@Param("t_ttid") int t_ttid);
}
