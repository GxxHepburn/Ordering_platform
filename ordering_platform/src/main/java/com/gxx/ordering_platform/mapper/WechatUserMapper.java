package com.gxx.ordering_platform.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.Multi_WechatUser_Orders;
import com.gxx.ordering_platform.entity.NUS;
import com.gxx.ordering_platform.entity.UDS;
import com.gxx.ordering_platform.entity.WechatUser;

public interface WechatUserMapper {

	@Select("SELECT * FROM wechat_user WHERE U_OpenId = #{u_openid}")
	WechatUser getByUOpenId(@Param("u_openid") String u_openid);
	
	@Update("UPDATE wechat_user SET U_LoginTime = #{wechatUser.U_LoginTime} WHERE U_OpenId = #{wechatUser.U_OpenId}")
	boolean updateLoginTimeByOpenId(@Param("wechatUser") WechatUser wechatUser);
	
	@Insert("INSERT INTO wechat_user (U_OpenId, U_RegisterTime, U_LoginTime, U_Status) VALUES (#{wechatUser.U_OpenId}, "
			+ "#{wechatUser.U_RegisterTime}, #{wechatUser.U_LoginTime}, 1)")
	boolean insert(@Param("wechatUser") WechatUser wechatUser);
	
	@Select("<script>"
			+ "SELECT U_ID, U_OpenId, U_RegisterTime, U_LoginTime, U_Status, MAX(O_OrderingTime) AS O_OrderingTime from orders INNER JOIN wechat_user "
			+ "WHERE 1=1"
			+ " AND wechat_user.U_ID = orders.O_UID"
			+ " AND orders.O_MID = #{o_mid}"
			+ "<if test='u_openid!=null'>"
			+ " AND U_OpenId =#{u_openid}"
			+ "</if>"
			+ " GROUP BY wechat_user.U_ID ORDER BY orders.O_OrderingTime limit #{limitStart}, #{pagesize}"
			+ "</script>")
	List<Multi_WechatUser_Orders> getByUOpenIdLike(@Param("o_mid") int o_mid, @Param("u_openid") String u_openid, @Param("limitStart") int limitStart, @Param("pagesize") int pagesize);
	
	@Select("<script>"
			+ "SELECT COUNT(DISTINCT U_ID) FROM orders INNER JOIN wechat_user "
			+ "WHERE 1=1"
			+ " AND wechat_user.U_ID = orders.O_UID"
			+ " AND orders.O_MID = #{o_mid}"
			+ "<if test='u_openid!=null'>"
			+ " AND U_OpenId =#{u_openid}"
			+ "</if>"
			+ "</script>")
	int getTotalByOpenIdLike(@Param("o_mid") int o_mid, @Param("u_openid") String u_openid);
	
	@Update("UPDATE wechat_user SET U_Status = #{u_status} WHERE U_ID = #{u_id}")
	void changStatusByUID(@Param("u_id") int u_id, @Param("u_status") int u_status);
	
	@Select("<script>"
			+ "SELECT U_ID, U_OpenId, U_RegisterTime, U_LoginTime, U_Status, MAX(O_OrderingTime) AS O_OrderingTime from orders INNER JOIN wechat_user "
			+ "WHERE 1=1"
			+ " AND wechat_user.U_ID = orders.O_UID"
			+ " AND orders.O_MID = #{o_mid}"
			+ "<if test='u_id!=null'>"
			+ " AND U_ID =#{u_id}"
			+ "</if>"
			+ " GROUP BY wechat_user.U_ID ORDER BY orders.O_OrderingTime limit #{limitStart}, #{pagesize}"
			+ "</script>")
	List<Multi_WechatUser_Orders> getByUID(@Param("o_mid") int o_mid, @Param("u_id") Integer u_id, @Param("limitStart") int limitStart, @Param("pagesize") int pagesize);
	
	@Select("<script>"
			+ "SELECT COUNT(DISTINCT U_ID) FROM orders INNER JOIN wechat_user "
			+ "WHERE 1=1"
			+ " AND wechat_user.U_ID = orders.O_UID"
			+ " AND orders.O_MID = #{o_mid}"
			+ "<if test='u_id!=null'>"
			+ " AND U_ID =#{u_id}"
			+ "</if>"
			+ "</script>")
	int getTotalByUID(@Param("o_mid") int o_mid,  @Param("u_id") Integer u_id);
	
	@Select("SELECT COUNT(DISTINCT orders.O_UID) as userNum FROM orders WHERE "
			+ " orders.O_MID = #{m_id} "
			+ " AND orders.O_OrderingTime >= #{dateStart} "
			+ " AND orders.O_OrderingTime <= #{dateEnd}")
	UDS searchUDSUserNum(@Param("m_id") int m_id, @Param("dateStart") Date dateStart, @Param("dateEnd") Date dateEnd);
	
	@Select("SELECT COUNT(*) as newUserNum FROM wechat_user WHERE wechat_user.U_ID IN (SELECT DISTINCT orders.O_UID FROM orders WHERE "
			+ " orders.O_MID = #{m_id} "
			+ " AND orders.O_OrderingTime >= #{dateStart} "
			+ " AND orders.O_OrderingTime <= #{dateEnd}) "
			+ " AND wechat_user.U_ID NOT IN ("
			+ " SELECT DISTINCT orders.O_UID FROM orders WHERE "
			+ " orders.O_MID = #{m_id} "
			+ " AND orders.O_OrderingTime < #{dateStart})")
	UDS searchUDSNewUserNum(@Param("m_id") int m_id, @Param("dateStart") Date dateStart, @Param("dateEnd") Date dateEnd);
	
	@Select("SELECT SUM(orders.O_NumberOfDiners) as consumeNum, COUNT(*) as consumeCount, "
			+ " SUM(orders.O_TotlePrice) as totalPrice "
			+ " FROM orders WHERE "
			+ " orders.O_MID = #{m_id} "
			+ " AND orders.O_OrderingTime >= #{dateStart} "
			+ " AND orders.O_OrderingTime <= #{dateEnd}")
	UDS searchUDSConsume(@Param("m_id") int m_id, @Param("dateStart") Date dateStart, @Param("dateEnd") Date dateEnd);
	
	@Select("SELECT COUNT(*) as newUserNum FROM wechat_user WHERE wechat_user.U_ID IN (SELECT DISTINCT orders.O_UID FROM orders WHERE "
			+ " orders.O_MID = #{m_id} "
			+ " AND orders.O_OrderingTime >= #{dateStart} "
			+ " AND orders.O_OrderingTime <= #{dateEnd}) "
			+ " AND wechat_user.U_ID NOT IN ("
			+ " SELECT DISTINCT orders.O_UID FROM orders WHERE "
			+ " orders.O_MID = #{m_id} "
			+ " AND orders.O_OrderingTime < #{dateStart})")
	NUS searchNUS(@Param("m_id") int m_id, @Param("dateStart") Date dateStart, @Param("dateEnd") Date dateEnd);
}
