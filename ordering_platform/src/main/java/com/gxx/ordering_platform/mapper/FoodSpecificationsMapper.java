package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.FoodSpecifications;

public interface FoodSpecificationsMapper {

	@Select("SELECT * FROM foodspecifications WHERE"
			+ " FS_MID = #{fs_mid} AND FS_FTID = #{fs_ftid} AND FS_FID = #{fs_fid}")
	List<FoodSpecifications> getByMIDAndFTIDAndFID(@Param("fs_mid") int fs_mid,
			@Param("fs_ftid") int fs_ftid, @Param("fs_fid") int fs_fid);
	
	@Delete("DELETE FROM foodspecifications WHERE FS_FID = #{fs_fid}")
	void deleteByFS_FID(@Param("fs_fid") int fs_fid);
	
	@Insert("INSERT INTO foodspecifications (FS_FID, FS_FTID, FS_MID, FS_Key, FS_Value) VALUES"
			+ " (#{fs_fid}, #{fs_ftid}, #{fs_mid}, #{fs_key}, #{fs_value})")
	void insert(@Param("fs_fid") int fs_fid, @Param("fs_ftid") int fs_ftid, @Param("fs_mid") int fs_mid,
			 @Param("fs_key") String fs_key, @Param("fs_value") float fs_value);
	
	@Delete("DELETE FROM foodspecifications WHERE FS_FTID = #{fs_ftid}")
	void deleteByFTID(@Param("fs_ftid") int fs_ftid);
}
