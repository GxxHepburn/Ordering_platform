package com.gxx.ordering_platform.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.gxx.ordering_platform.AppConfig;
import com.gxx.ordering_platform.entity.FoodType;
import com.gxx.ordering_platform.mapper.FoodTypeMapper;

//创建spring容器
//创建web容器
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes= {AppConfig.class})
public class WeChatInitMenuServiceTest {

	@Autowired
	FoodTypeMapper foodTypeMapper;
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Test
	public void initMenuTest() {
		String res = "1";
		//通过与数据库交互，获取初始化菜单所需数据
		int ft_mid = Integer.valueOf(res);
		logger.info("go");
		List<FoodType> foodTypes = foodTypeMapper.getByFTMID(ft_mid);
		foodTypes.stream().forEach(food -> System.out.println(food.getFT_Name()));
	}
}
