package com.gxx.ordering_platform.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.CSS;
import com.gxx.ordering_platform.entity.FoodType;
import com.gxx.ordering_platform.entity.PSC;
import com.gxx.ordering_platform.entity.PSS;

public interface FoodTypeMapper {

	@Select("SELECT * FROM foodtype WHERE FT_MID = #{ft_mid}")
	List<FoodType> getByFTMID(@Param("ft_mid") int ft_mid);
	
	@Select("SELECT * FROM foodtype WHERE FT_ID = #{f_ftid}")
	FoodType getByFTID(@Param("f_ftid") int f_ftid);
	
	@Select("SELECT * FROM foodtype WHERE FT_MID = #{ft_mid} AND FT_Name like concat(#{query}, '%') limit #{limitStart}, #{pagesize}")
	List<FoodType> getByFTMIDWithQuery(@Param("ft_mid") int ft_mid, @Param("limitStart") int limitStart, @Param("pagesize") int pagesize, @Param("query") String query);
	
	@Select("SELECT COUNT(*) FROM foodtype WHERE FT_MID = #{ft_mid} AND FT_Name like concat(#{query}, '%')")
	int getTotalByFTMIDWithQuery(@Param("ft_mid") int ft_mid, @Param("query") String query);
	
	@Delete("DELETE FROM foodtype WHERE FT_ID = #{ft_id}")
	void deleteByFTID(@Param("ft_id") int ft_id);
	
	@Update("UPDATE foodtype SET FT_Name = #{ft_name} WHERE FT_ID = #{ft_id}")
	void updateFTNameByFTID(@Param("ft_id") int ft_id, @Param("ft_name") String ft_name);
	
	@Insert("INSERT INTO foodtype (FT_MID, FT_Name) VALUES (#{ft_mid}, #{ft_name})")
	void insert(@Param("ft_mid") int ft_mid, @Param("ft_name") String ft_name);
	
	@Select("<script>"
			+ "SELECT foodtype.FT_ID as ftid, foodtype.FT_Name as ftname, "
			+ " food.F_ID as fid,  food.F_Name as fname, SUM(orderdetail.OD_Num) as odnum, "
			+ " SUM(orderdetail.OD_RealNum) as odrealnum , orderdetail.OD_RealPrice as odrealprice, "
			+ " SUM(orderdetail.OD_RealNum) * orderdetail.OD_RealPrice as realTotalPrice, "
			+ " SUM(orderdetail.OD_Num) * orderdetail.OD_RealPrice as totalPrice "
			+ " FROM orderdetail left join food on orderdetail.OD_FID = food.F_ID "
			+ " left join foodtype on food.F_FTID = foodtype.FT_ID "
			+ " left join orderadd on orderdetail.OD_OAID = orderadd.OA_ID "
			+ " WHERE orderadd.OA_MID = #{m_id} AND orderadd.OA_OrderingTime &gt;= #{pssStartDate} "
			+ " AND orderadd.OA_OrderingTime &lt;= #{pssEndDate} "
			+ "<if test='foodId!=null'>"
			+ " AND orderdetail.OD_FID = #{foodId}"
			+ "</if>"
			+ "<if test='foodtypeId!=null'>"
			+ " AND foodtype.FT_ID = #{foodtypeId}"
			+ "</if>"
			+ " GROUP BY ftid, fid, odrealprice ORDER BY ftid, fid, odrealprice"
			+ "</script>")
	List<PSS> searchPSS(@Param("m_id") int m_id, @Param("pssStartDate") Date pssStartDate, 
			@Param("pssEndDate") Date pssEnDate, @Param("foodId") Integer foodId, @Param("foodtypeId") Integer foodtypeId);
	
	@Select("<script>"
			+ "SELECT foodtype.FT_ID as ftid, foodtype.FT_Name as ftname, "
			+ " SUM(orderdetail.OD_Num) as odnum, "
			+ " SUM(orderdetail.OD_Num * orderdetail.OD_RealPrice) as totalPrice "
			+ " FROM orderdetail left join food on orderdetail.OD_FID = food.F_ID "
			+ " left join foodtype on food.F_FTID = foodtype.FT_ID "
			+ " left join orderadd on orderdetail.OD_OAID = orderadd.OA_ID "
			+ " WHERE orderadd.OA_MID = #{m_id} AND orderadd.OA_OrderingTime &gt;= #{cssStartDate} "
			+ " AND orderadd.OA_OrderingTime &lt;= #{cssEndDate} "
			+ "<if test='foodtypeId!=null'>"
			+ " AND foodtype.FT_ID = #{foodtypeId}"
			+ "</if>"
			+ " GROUP BY ftid"
			+ "</script>")
	List<CSS> searchCSS(@Param("m_id") int m_id, @Param("cssStartDate") Date cssStartDate, 
			@Param("cssEndDate") Date cssEnDate, @Param("foodtypeId") Integer foodtypeId);
	
	@Select("<script>"
			+ "SELECT foodtype.FT_ID as ftid, foodtype.FT_Name as ftname, "
			+ " food.F_ID as fid,  food.F_Name as fname, orderdetail.OD_Spec as spec, "
			+ " orderdetail.OD_PropOne as propOne, orderdetail.OD_PropTwo as propTwo, "
			+ " SUM(orderdetail.OD_Num) as odnum, "
			+ " SUM(orderdetail.OD_RealNum) as odrealnum , orderdetail.OD_RealPrice as odrealprice, "
			+ " SUM(orderdetail.OD_RealNum) * orderdetail.OD_RealPrice as realTotalPrice, "
			+ " SUM(orderdetail.OD_Num) * orderdetail.OD_RealPrice as totalPrice "
			+ " FROM orderdetail left join food on orderdetail.OD_FID = food.F_ID "
			+ " left join foodtype on food.F_FTID = foodtype.FT_ID "
			+ " left join orderadd on orderdetail.OD_OAID = orderadd.OA_ID "
			+ " WHERE orderadd.OA_MID = #{m_id} AND orderadd.OA_OrderingTime &gt;= #{pscStartDate} "
			+ " AND orderadd.OA_OrderingTime &lt;= #{pscEndDate} "
			+ "<if test='foodId!=null'>"
			+ " AND orderdetail.OD_FID = #{foodId}"
			+ "</if>"
			+ "<if test='foodtypeId!=null'>"
			+ " AND foodtype.FT_ID = #{foodtypeId}"
			+ "</if>"
			+ " GROUP BY ftid, fid, spec, propOne, propTwo ORDER BY ftid, fid, odrealprice, propOne, propTwo"
			+ "</script>")
	List<PSC> searchPSC(@Param("m_id") int m_id, @Param("pscStartDate") Date pscStartDate, 
			@Param("pscEndDate") Date pscEnDate, @Param("foodId") Integer foodId, @Param("foodtypeId") Integer foodtypeId);
}
