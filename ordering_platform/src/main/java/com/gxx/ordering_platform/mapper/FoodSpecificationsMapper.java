package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.FoodSpecifications;

public interface FoodSpecificationsMapper {

	@Select("SELECT * FROM foodspecifications WHERE"
			+ " FS_MID = #{fs_mid} AND FS_FTID = #{fs_ftid} AND FS_FID = #{fs_fid}")
	List<FoodSpecifications> getByMIDAndFTIDAndFID(@Param("fs_mid") int fs_mid,
			@Param("fs_ftid") int fs_ftid, @Param("fs_fid") int fs_fid);
}
