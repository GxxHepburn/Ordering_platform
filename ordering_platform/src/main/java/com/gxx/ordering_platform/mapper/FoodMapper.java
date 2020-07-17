package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.Food;

public interface FoodMapper {

	@Select("SELECT * FROM food WHERE F_FTID = #{f_ftid} AND F_MID = #{f_mid}")
	List<Food> getByMIDAndFTID(@Param("f_ftid") int f_ftid,@Param("f_mid") int f_mid);
	
	@Select("SELECT F_Stock FROM food WHERE F_ID = #{f_id}")
	@ResultType(Integer.class)
	Integer getStockByFID(@Param("f_id") int f_id);
	
	@Select("SELECT F_SalesVolume FROM food WHERE F_ID = #{f_id}")
	@ResultType(Integer.class)
	Integer getSalesNumByFID(@Param("f_id") int f_id);
	
	//修改库存
	@Update("UPDATE food SET F_Stock = #{f_stock} WHERE F_ID = #{f_id}")
	boolean updateStockByFID(@Param("f_stock") int f_stock,@Param("f_id") int f_id);
	
	//修改销量
	@Update("UPDATE food SET F_SalesVolume = #{f_salesvolume} WHERE F_ID = #{f_id}")
	boolean updateSalesVolumeByFID(@Param("f_salesvolume") int f_salesvolume,@Param("f_id") int f_id);
}
