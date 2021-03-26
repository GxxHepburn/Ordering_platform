package com.gxx.ordering_platform.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.Mmngct;

public interface MmaMapper {

	@Select("SELECT * FROM mmngct WHERE MMA_UserName = #{mma_username}")
	Mmngct getByUsername(@Param("mma_username") String mma_username);
	
	@Select("SELECT * FROM mmngct WHERE MMA_MID = #{mma_mid}")
	List<Mmngct> getByM_ID(@Param("mma_mid") int mma_mid);
	
	@Update("UPDATE mmngct set MMA_LastLoginTime = #{mma_lastLoginTime} WHERE MMA_UserName = #{mma_username}")
	void updateLastLoginTime(@Param("mma_username") String mma_username, @Param("mma_lastLoginTime") Date mma_lastLoginTime);
	
	@Update("UPDATE mmngct set MMA_Password = #{password} WHERE MMA_UserName = #{username}")
	void updatePassword(@Param("username") String username, @Param("password") String password);
}
