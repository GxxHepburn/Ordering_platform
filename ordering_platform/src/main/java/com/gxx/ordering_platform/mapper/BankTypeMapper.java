package com.gxx.ordering_platform.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.BankType;

public interface BankTypeMapper {
	
	@Select("SELECT * FROM banktype WHERE B_CharCode = #{p_bank_type}")
	BankType getByB_CharCode(@Param("p_bank_type") String p_bank_type);
}
