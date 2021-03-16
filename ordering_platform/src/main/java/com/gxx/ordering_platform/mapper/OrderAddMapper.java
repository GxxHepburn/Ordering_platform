package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.Multi_OrderAdd_Tab_Tabtype_Orders;
import com.gxx.ordering_platform.entity.OrderAdd;

public interface OrderAddMapper {

	@Insert("INSERT INTO orderadd (OA_OID, OA_Sort, OA_MID, OA_UID, OA_TID, OA_OrderingTime, "
			+ "OA_TotleNum, OA_IsTaking) VALUES (#{orderAdd.OA_OID}, #{orderAdd.OA_Sort}, #{orderAdd.OA_MID}, "
			+ "#{orderAdd.OA_UID}, #{orderAdd.OA_TID}, #{orderAdd.OA_OrderingTime}, #{orderAdd.OA_TotleNum}, #{orderAdd.OA_IsTaking})")
	@Options(useGeneratedKeys = true, keyProperty = "OA_ID")
	int insert(@Param("orderAdd") OrderAdd orderAdd);
	
	@Update("UPDATE orderadd SET OA_TotlePrice = #{oa_totlePrice} WHERE OA_ID = #{oa_id}")
	void updateTotlePrice(@Param("oa_id") int oa_id, @Param("oa_totlePrice") float oa_totlePrice);
	
	@Select("SELECT COUNT(*) FROM orderadd WHERE OA_OID = #{oa_oid}")
	int selectCountByOA_OID(@Param("oa_oid") int oa_oid);
	
	@Select("SELECT * FROM orderadd WHERE OA_OID = #{oa_oid} ORDER BY OA_Sort")
	List<OrderAdd> getByO_ID(@Param("oa_oid") int oa_oid);
	
	@Update("UPDATE orderadd SET OA_IsTaking = #{oa_isTaking} WHERE OA_ID = #{oa_id}")
	void updateOA_IsTakingByOA_ID(@Param("oa_id") int oa_id, @Param("oa_isTaking") String oa_isTaking);
	
	@Select("SELECT * "
			+ "FROM orderadd left join tab on tab.T_ID = orderadd.OA_TID left join tabtype on tabtype.TT_ID = tab.T_TTID left join orders on orderadd.OA_OID = orders.O_ID "
			+ "WHERE OA_MID = #{m_id} "
			+ "AND OA_IsTaking = '0' "
			+ "ORDER BY OA_OrderingTime "
			+ "limit #{limitStart}, #{pagesize}")
	List<Multi_OrderAdd_Tab_Tabtype_Orders> getNotTakingByMIDOrderByOrderingTime(@Param("m_id") int m_id, @Param("limitStart") int limitStart, @Param("pagesize") int pagesize);
	
	@Select("<script>"
			+ "SELECT * "
			+ "FROM orderadd left join tab on tab.T_ID = orderadd.OA_TID left join tabtype on tabtype.TT_ID = tab.T_TTID left join orders on orderadd.OA_OID = orders.O_ID "
			+ "WHERE OA_MID = #{m_id} "
			+ "<if test='tabId!=null'>"
			+ " AND tab.T_ID = #{tabId} "
			+ "</if>"
			+ " AND OA_IsTaking = '0' "
			+ " ORDER BY OA_OrderingTime "
			+ " limit #{limitStart}, #{pagesize}"
			+ "</script>")
	List<Multi_OrderAdd_Tab_Tabtype_Orders> getNotTakingByMIDTabIdOrderByOrderingTime(@Param("m_id") int m_id, @Param("tabId") Integer tabId, @Param("limitStart") int limitStart, @Param("pagesize") int pagesize);
	
	@Select("SELECT COUNT(*) FROM orderadd WHERE OA_MID = #{m_id} AND OA_IsTaking = '0'")
	int getNotTakingTotleByMIDOrder(@Param("m_id") int m_id);
}
