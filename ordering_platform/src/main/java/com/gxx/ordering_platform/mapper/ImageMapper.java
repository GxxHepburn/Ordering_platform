package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.Image;

public interface ImageMapper {

	@Select("SELECT * FROM image WHERE I_Hash_Uniq = #{mdString}")
	List<Image> getByI_Hash_Uniq(@Param("mdString") String mdString);
	
	@Insert("INSERT INTO image (I_Hash_Uniq, I_Name) VALUES (#{mdString}, #{i_name})")
	void insert(@Param("mdString") String mdString, @Param("i_name") String i_name);
}
