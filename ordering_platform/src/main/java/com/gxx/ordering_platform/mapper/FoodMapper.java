package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.Food;
import com.gxx.ordering_platform.entity.Orders;


public interface FoodMapper {

	@Select("SELECT * FROM food WHERE F_FTID = #{f_ftid} AND F_MID = #{f_mid}")
	List<Food> getByMIDAndFTID(@Param("f_ftid") int f_ftid,@Param("f_mid") int f_mid);
	
	@Select("SELECT * FROM food WHERE F_ID = #{f_id}")
	Food getByFoodId(@Param("f_id") int f_id);
	
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
	
	@Select("SELECT * FROM food WHERE F_MID = #{f_mid} AND F_Name like concat(#{query}, '%') limit #{limitStart}, #{pagesize}")
	List<Food> getBtMID(@Param("f_mid") int f_mid, @Param("limitStart") int limitStart, @Param("pagesize") int pagesize, @Param("query") String query);
	
	@Select("SELECT * FROM food WHERE F_MID = #{f_mid} AND F_FTID = #{f_ftid} AND F_Name like concat(#{query}, '%') limit #{limitStart}, #{pagesize}")
	List<Food> getBtMIDWithFT_ID(@Param("f_mid") int f_mid, @Param("f_ftid") int f_ftid, @Param("limitStart") int limitStart, @Param("pagesize") int pagesize, @Param("query") String query);
	
	
	@Select("SELECT COUNT(*) FROM food WHERE F_MID = #{f_mid} AND F_Name like concat(#{query}, '%')")
	int getTotalByMID(@Param("f_mid") int f_mid, @Param("query") String query);
	
	// 修改商品信息
	@Update("UPDATE food SET F_FTID = #{f_ftid}, F_Name = #{f_name}, F_ImageUrl = #{f_imageUrl},"
			+ " F_Price = #{f_price}, F_Unit = #{f_unit}, F_Stock = #{f_stock}, F_Tag = #{f_tag} WHERE F_ID = #{f_id}")
	void updateFoodInfoByFID(@Param("f_ftid") int f_ftid, @Param("f_name") String f_name, @Param("f_imageUrl") String f_imageUrl,
			@Param("f_price") float f_price, @Param("f_unit") String f_unit, @Param("f_stock") int f_stock,
			@Param("f_tag") String f_tag, @Param("f_id") int f_id);
	
	@Update("UPDATE food SET F_FTID = #{f_ftid}, F_Name = #{f_name},"
			+ " F_Price = #{f_price}, F_Unit = #{f_unit}, F_Stock = #{f_stock}, F_Tag = #{f_tag} WHERE F_ID = #{f_id}")
	void updateFoodInfoWithoutImgUrlByFID(@Param("f_ftid") int f_ftid, @Param("f_name") String f_name,
			@Param("f_price") float f_price, @Param("f_unit") String f_unit, @Param("f_stock") int f_stock,
			@Param("f_tag") String f_tag, @Param("f_id") int f_id);
	
	//删除商品
	@Delete("DELETE FROM food WHERE F_ID = #{f_id}")
	void deleteFoodByFID(@Param("f_id") int f_id);
	
	// 添加商品
	@Insert("INSERT INTO food (F_FTID, F_MID, F_Name, F_ImageUrl, F_Price, F_Unit, F_Stock, F_Tag) VALUES (#{food.F_FTID},"
			+ " #{food.F_MID}, #{food.F_Name}, #{food.F_ImageUrl}, #{food.F_Price}, #{food.F_Unit}, #{food.F_Stock}, #{food.F_Tag})")
	@Options(useGeneratedKeys = true, keyProperty = "F_ID")
	Integer insert(@Param("food") Food food);
	
	@Select("SELECT * FROM food WHERE F_FTID = #{f_ftid} LIMIT 1")
	Food isCateNullByFTID(@Param("f_ftid") int f_ftid);
}
