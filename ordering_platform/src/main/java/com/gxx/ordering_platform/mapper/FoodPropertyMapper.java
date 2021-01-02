package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.FoodProperty;

public interface FoodPropertyMapper {

	@Select("SELECT * FROM foodproperty WHERE FP_MID = #{fp_mid} AND "
			+ "FP_FTID = #{fp_ftid} AND FP_FID = #{fp_fid}")
	List<FoodProperty> getByMIDAndFTIDAndFID(@Param("fp_mid") int fp_mid,
			@Param("fp_ftid") int fp_ftid, @Param("fp_fid") int fp_fid);
	
	@Delete("DELETE FROM foodproperty WHERE FP_FID = #{fp_fid}")
	void deleteByFP_FID(@Param("fp_fid") int fp_fid);
	
	@Delete("DELETE FROM foodproperty WHERE FP_FTID = #{fp_ftid}")
	void deleteByFTID(@Param("fp_ftid") int fp_ftid);
	
	@Insert("INSERT INTO foodproperty (FP_FID, FP_FTID, FP_MID, FP_Name, FP_ValueOne, FP_ValueTwo, FP_ValueThree, FP_ValueFour, FP_ValueFive) VALUES"
			+ " (#{fp_fid}, #{fp_ftid}, #{fp_mid}, #{fp_name}, #{fp_valueone}, #{fp_valuetwo}, #{fp_valuethree}, #{fp_valuefour}, #{fp_valuefive})")
	void insert(@Param("fp_fid") int fp_fid, @Param("fp_ftid") int fp_ftid, @Param("fp_mid") int fp_mid, @Param("fp_name") String fp_name,
			 @Param("fp_valueone") String fp_valueone, @Param("fp_valuetwo") String fp_valuetwo, @Param("fp_valuethree") String fp_valuethree,
			 @Param("fp_valuefour") String fp_valuefour, @Param("fp_valuefive") String fp_valuefive);
	
	@Insert("INSERT INTO foodproperty (FP_FID, FP_FTID, FP_MID, FP_Name, FP_ValueOne, FP_ValueTwo, FP_ValueThree, FP_ValueFour) VALUES"
			+ " (#{fp_fid}, #{fp_ftid}, #{fp_mid}, #{fp_name}, #{fp_valueone}, #{fp_valuetwo}, #{fp_valuethree}, #{fp_valuefour})")
	void insertFour(@Param("fp_fid") int fp_fid, @Param("fp_ftid") int fp_ftid, @Param("fp_mid") int fp_mid, @Param("fp_name") String fp_name,
			 @Param("fp_valueone") String fp_valueone, @Param("fp_valuetwo") String fp_valuetwo, @Param("fp_valuethree") String fp_valuethree,
			 @Param("fp_valuefour") String fp_valuefour);
	
	@Insert("INSERT INTO foodproperty (FP_FID, FP_FTID, FP_MID, FP_Name, FP_ValueOne, FP_ValueTwo, FP_ValueThree) VALUES"
			+ " (#{fp_fid}, #{fp_ftid}, #{fp_mid}, #{fp_name}, #{fp_valueone}, #{fp_valuetwo}, #{fp_valuethree})")
	void insertThree(@Param("fp_fid") int fp_fid, @Param("fp_ftid") int fp_ftid, @Param("fp_mid") int fp_mid, @Param("fp_name") String fp_name,
			 @Param("fp_valueone") String fp_valueone, @Param("fp_valuetwo") String fp_valuetwo, @Param("fp_valuethree") String fp_valuethree);
	
	@Insert("INSERT INTO foodproperty (FP_FID, FP_FTID, FP_MID, FP_Name, FP_ValueOne, FP_ValueTwo) VALUES"
			+ " (#{fp_fid}, #{fp_ftid}, #{fp_mid}, #{fp_name}, #{fp_valueone}, #{fp_valuetwo})")
	void insertTwo(@Param("fp_fid") int fp_fid, @Param("fp_ftid") int fp_ftid, @Param("fp_mid") int fp_mid, @Param("fp_name") String fp_name,
			 @Param("fp_valueone") String fp_valueone, @Param("fp_valuetwo") String fp_valuetwo);
	
	@Insert("INSERT INTO foodproperty (FP_FID, FP_FTID, FP_MID, FP_Name, FP_ValueOne) VALUES"
			+ " (#{fp_fid}, #{fp_ftid}, #{fp_mid}, #{fp_name}, #{fp_valueone})")
	void insertOne(@Param("fp_fid") int fp_fid, @Param("fp_ftid") int fp_ftid, @Param("fp_mid") int fp_mid, @Param("fp_name") String fp_name,
			 @Param("fp_valueone") String fp_valueone);
	
}
