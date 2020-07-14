package com.gxx.ordering_platform.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.TabType;

public interface TabTypeMapper {

	@Select("SELECT * FROM tabtype WHERE TT_ID = #{tt_id}")
	TabType getByTabTypeId(@Param("tt_id") int tt_id);
}
