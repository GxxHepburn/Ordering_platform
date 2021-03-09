package com.gxx.ordering_platform.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.OrderReturn;
import com.gxx.ordering_platform.entity.RC;
import com.gxx.ordering_platform.entity.RS;
import com.gxx.ordering_platform.entity.ReturnOrdersPTimes;

public interface OrderReturnMapper {

	@Select("SELECT COUNT(*) FROM orderreturn WHERE OR_OID = #{or_oid}")
	int selectCountByOR_OID(@Param("or_oid") int or_oid);
	
	@Insert("INSERT INTO orderreturn (OR_OID, OR_Sort, OR_MID, OR_UID, OR_TID, OR_ReturnTime) VALUES ("
			+ "#{orderReturn.OR_OID}, #{orderReturn.OR_Sort}, #{orderReturn.OR_MID}, #{orderReturn.OR_UID}, "
			+ "#{orderReturn.OR_TID}, #{orderReturn.OR_ReturnTime})")
	@Options(useGeneratedKeys = true, keyProperty = "OR_ID")
	void insert(@Param("orderReturn") OrderReturn orderReturn);
	
	@Update("UPDATE orderreturn SET OR_TotlePrice = #{or_totlePrice}, OR_TotleReturnNum = #{or_totalNum} WHERE OR_ID = #{or_id}")
	void updateTotlePrice(@Param("or_id") int or_id, @Param("or_totlePrice") float or_totlePrice, @Param("or_totalNum") int or_totalNum);
	
	@Select("SELECT * FROM orderreturn WHERE OR_OID = #{o_id}")
	List<OrderReturn> getByO_ID(@Param("o_id") int o_id);
	
	@Select("SELECT DATE_FORMAT(OR_ReturnTime, '%Y-%m') as times, "
			+ " COUNT(*) as totalRefundNumbers, "
			+ " SUM(OR_TotleReturnNum) as totalReturnNum, "
			+ " SUM(OR_TotlePrice) as totalRefundPrice "
			+ " FROM orderreturn WHERE OR_MID = #{m_id} "
			+ " AND OR_ReturnTime >= #{dateStart} AND OR_ReturnTime <= #{dateEnd} "
			+ " GROUP BY times ORDER BY times")
	List<ReturnOrdersPTimes> searchReturnOrdersPMonth(@Param("m_id") int m_id, @Param("dateStart") Date dateStart, @Param("dateEnd") Date dateEnd);
	
	@Select("SELECT orders.O_UniqSearchID as ousid ,"
			+ " tabtype.TT_ID as ttid, tabtype.TT_Name as ttname, "
			+ " tab.T_ID as tid, tab.T_Name as tname, "
			+ " orderreturndetail.ORD_Spec as spec, "
			+ " orderreturndetail.ORD_PropOne as propOne, "
			+ " orderreturndetail.ORD_PropTwo as propTwo, "
			+ " foodtype.FT_ID as ftid, foodtype.FT_Name as ftname, "
			+ " food.F_ID as fid, food.F_Name as fname, "
			+ " food.F_Unit as unit, orderreturndetail.ORD_RealPrice as price, "
			+ " orderreturndetail.ORD_Num as num, "
			+ " orderreturndetail.ORD_Num * orderreturndetail.ORD_RealPrice as totalPrice, "
			+ " orders.O_OrderingTime as orderTime, orderreturn.OR_ReturnTime as returnTime "
			+ " FROM orderreturndetail left join orderreturn on orderreturndetail.ORD_ORID = orderreturn.OR_ID "
			+ " left join orders on orderreturndetail.ORD_OID = orders.O_ID "
			+ " left join food on orderreturndetail.ORD_FID = food.F_ID "
			+ " left join foodtype on food.F_FTID = foodtype.FT_ID "
			+ " left join tab on orderreturn.OR_TID = tab.T_ID "
			+ " left join tabtype on tab.T_TTID = tabtype.TT_ID "
			+ " WHERE orderreturn.OR_MID = #{m_id} "
			+ " AND orderreturn.OR_ReturnTime >= #{dateStart} "
			+ " AND orderreturn.OR_ReturnTime <= #{dateEnd} "
			+ " ORDER BY orderreturn.OR_ReturnTime")
	List<RC> searchRCWithoutOUID(@Param("m_id") int m_id, @Param("dateStart") Date dateStart, @Param("dateEnd") Date dateEnd);
	
	@Select("SELECT orders.O_UniqSearchID as ousid ,"
			+ " tabtype.TT_ID as ttid, tabtype.TT_Name as ttname, "
			+ " tab.T_ID as tid, tab.T_Name as tname, "
			+ " orderreturndetail.ORD_Spec as spec, "
			+ " orderreturndetail.ORD_PropOne as propOne, "
			+ " orderreturndetail.ORD_PropTwo as propTwo, "
			+ " foodtype.FT_ID as ftid, foodtype.FT_Name as ftname, "
			+ " food.F_ID as fid, food.F_Name as fname, "
			+ " food.F_Unit as unit, orderreturndetail.ORD_RealPrice as price, "
			+ " orderreturndetail.ORD_Num as num, "
			+ " orderreturndetail.ORD_Num * orderreturndetail.ORD_RealPrice as totalPrice, "
			+ " orders.O_OrderingTime as orderTime, orderreturn.OR_ReturnTime as returnTime "
			+ " FROM orderreturndetail left join orderreturn on orderreturndetail.ORD_ORID = orderreturn.OR_ID "
			+ " left join orders on orderreturndetail.ORD_OID = orders.O_ID "
			+ " left join food on orderreturndetail.ORD_FID = food.F_ID "
			+ " left join foodtype on food.F_FTID = foodtype.FT_ID "
			+ " left join tab on orderreturn.OR_TID = tab.T_ID "
			+ " left join tabtype on tab.T_TTID = tabtype.TT_ID "
			+ " WHERE orderreturn.OR_MID = #{m_id} "
			+ " AND orders.O_UniqSearchID = #{ousid} "
			+ " ORDER BY orderreturn.OR_ReturnTime")
	List<RC> searchRCWithOUID(@Param("m_id") int m_id, @Param("ousid") String ousid);
	
//	@Select("<script>"
//			+ "SELECT orderreturndetail.ORD_Spec as spec, "
//			+ " orderreturndetail.ORD_PropOne as propOne, "
//			+ " orderreturndetail.ORD_PropTwo as propTwo, "
//			+ " foodtype.FT_ID as ftid, foodtype.FT_Name as ftname, "
//			+ " food.F_ID as fid, food.F_Name as fname, food.F_Unit as unit, "
//			+ " orderreturndetail.ORD_RealPrice as price, "
//			+ " SUM(orderreturndetail.ORD_Num) as num, "
//			+ " SUM(orderreturndetail.ORD_Num * orderreturndetail.ORD_RealPrice) as totalPrice, "
//			+ " orders.O_ID as oid "
//			+ " FROM orderreturndetail left join orderreturn on orderreturndetail.ORD_ORID = orderreturn.OR_ID "
//			+ " left join orders on orderreturndetail.ORD_OID = orders.O_ID "
//			+ " left join food on orderreturndetail.ORD_FID = food.F_ID "
//			+ " left join foodtype on food.F_FTID = foodtype.FT_ID "
//			+ " WHERE orderreturn.OR_MID = #{m_id} "
//			+ " AND orderreturn.OR_ReturnTime &gt;= #{dateStart} "
//			+ " AND orderreturn.OR_ReturnTime &lt;= #{dateEnd} "
//			+ " GROUP BY ftname, fname, spec, propOne, propTwo "
//			+ " ORDER BY ftname, fname, price, propOne, propTwo"
//			+ "</script>")
	@Select("<script>"
			+ "SELECT returnsku.spec, returnsku.propOne, returnsku.propTwo, "
			+ " returnsku.ftid, returnsku.ftname, returnsku.fid, returnsku.fname, "
			+ " returnsku.unit, returnsku. price, returnsku.num, returnsku.totalPrice, "
			+ " ordersku.orderNum as orderNum FROM "
			+ ""
			+ "(SELECT orderreturndetail.ORD_Spec as spec, "
			+ " orderreturndetail.ORD_PropOne as propOne, "
			+ " orderreturndetail.ORD_PropTwo as propTwo, "
			+ " foodtype.FT_ID as ftid, foodtype.FT_Name as ftname, "
			+ " food.F_ID as fid, food.F_Name as fname, food.F_Unit as unit, "
			+ " orderreturndetail.ORD_RealPrice as price, "
			+ " SUM(orderreturndetail.ORD_Num) as num, "
			+ " SUM(orderreturndetail.ORD_Num * orderreturndetail.ORD_RealPrice) as totalPrice, "
			+ " orders.O_ID as oid "
			+ " FROM orderreturndetail left join orderreturn on orderreturndetail.ORD_ORID = orderreturn.OR_ID "
			+ " left join orders on orderreturndetail.ORD_OID = orders.O_ID "
			+ " left join food on orderreturndetail.ORD_FID = food.F_ID "
			+ " left join foodtype on food.F_FTID = foodtype.FT_ID "
			+ " WHERE orderreturn.OR_MID = #{m_id} "
			+ " AND orderreturn.OR_ReturnTime &gt;= #{dateStart} "
			+ " AND orderreturn.OR_ReturnTime &lt;= #{dateEnd} "
			+ " GROUP BY ftname, fname, spec, propOne, propTwo) returnsku "
			+ ""
			+ " LEFT JOIN "
			+ ""
			+ "(SELECT SUM(orderdetail.OD_Num) as orderNum, food.F_ID as ofid, "
			+ " food.F_Name as ofname, foodtype.FT_ID as oftid, foodtype.FT_Name as oftname, "
			+ " orderdetail.OD_Spec as ospec, orderdetail.OD_PropOne as opropOne, "
			+ " orderdetail.OD_PropTwo as opropTwo "
			+ " FROM orderdetail left join orders on orderdetail.OD_OID = orders.O_ID "
			+ " left join food on food.F_ID = orderdetail.OD_FID "
			+ " left join foodtype on food.F_FTID = foodtype.FT_ID"
			+ " WHERE orders.O_ID IN "
			+ ""
			+ " (SELECT DISTINCT orders.O_ID "
			+ " FROM orderreturn left join orders on orderreturn.OR_OID = orders.O_ID "
			+ " WHERE orders.O_MID = #{m_id} AND orderreturn.OR_ReturnTime &gt;= #{dateStart} "
			+ " AND orderreturn.OR_ReturnTime &lt;= #{dateEnd}) "
			+ ""
			+ " GROUP BY oftname, ofname, ospec, opropOne, opropTwo)"
			+ " ordersku "
			+ ""
			+ " on returnsku.spec = ordersku.ospec AND "
			+ " returnsku.propOne = ordersku.opropOne AND "
			+ " returnsku.propTwo = ordersku.opropTwo AND "
			+ " returnsku.ftid = ordersku.oftid AND "
			+ " returnsku.fid = ordersku.ofid "
			+ " ORDER BY returnsku.ftname, returnsku.fname, returnsku.price, returnsku.propOne, returnsku.propTwo"
			+ "</script>")
	List<RS> searchRS(@Param("m_id") int m_id, @Param("dateStart") Date dateStart, @Param("dateEnd") Date dateEnd);
}
