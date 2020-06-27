package com.gxx.ordering_platform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.WechatUser;

public interface WechatUserMapper {

	@Select("SELECT * FROM wechat_user WHERE U_OpenId = #{u_openid}")
	WechatUser getByUOpenId(@Param("u_openid") String u_openid);
	
	@Update("UPDATE wechat_user SET U_LoginTime = #{wechatUser.U_LoginTime} WHERE U_OpenId = #{wechatUser.U_OpenId}")
	boolean updateLoginTimeByOpenId(@Param("wechatUser") WechatUser wechatUser);
	
	@Insert("INSERT INTO wechat_user (U_OpenId, U_RegisterTime, U_LoginTime) VALUES (#{wechatUser.U_OpenId}, "
			+ "#{wechatUser.U_RegisterTime}, #{wechatUser.U_LoginTime})")
	boolean insert(@Param("wechatUser") WechatUser wechatUser);
}
