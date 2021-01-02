package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.TabType;

public interface TabTypeMapper {

	@Select("SELECT * FROM tabtype WHERE TT_ID = #{tt_id}")
	TabType getByTabTypeId(@Param("tt_id") int tt_id);
	
	@Select("SELECT * FROM tabtype WHERE TT_MID = #{tt_mid}")
	List<TabType> getByMID(@Param("tt_mid") int tt_mid);
	
	@Select("SELECT * FROM tabtype WHERE TT_MID = #{tt_mid} AND TT_Name like concat(#{query}, '%') limit #{limitStart}, #{pagesizeInt}")
	List<TabType> getByMIDWithQuery(@Param("tt_mid") int tt_mid, @Param("limitStart") int limitStart, @Param("pagesizeInt") int pagesizeInt, @Param("query") String query);
	
	@Select("SELECT COUNT(*) FROM tabtype WHERE TT_MID = #{tt_mid} AND TT_Name like concat(#{query}, '%')")
	int getTotalByFTMIDWithQuery(@Param("tt_mid") int tt_mid, @Param("query") String query);
	
	@Delete("DELETE FROM tabtype WHERE TT_ID = #{tt_id}")
	void deleteByTTID(@Param("tt_id") int tt_id);
	
	@Update("UPDATE tabtype SET TT_Name = #{tt_name} WHERE TT_ID = #{tt_id}")
	void updateTTNameByTTID (@Param("tt_id") int tt_id, @Param("tt_name") String tt_name);
	
	@Insert("INSERT INTO tabtype (TT_MID, TT_Name) VALUES (#{tt_mid}, #{tt_name})")
	void insert(@Param("tt_mid") int tt_mid, @Param("tt_name") String tt_name);
}
