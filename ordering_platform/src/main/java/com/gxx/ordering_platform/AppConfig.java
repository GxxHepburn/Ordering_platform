 package com.gxx.ordering_platform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.gxx.ordering_platform.filter.WechatOpenIdFilter;
import com.gxx.ordering_platform.reamls.ShiroRealm;
import com.gxx.ordering_platform.service.WechatLoginService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan
@MapperScan("com.gxx.ordering_platform.mapper")
@EnableWebMvc
@EnableTransactionManagement
@PropertySource({"classpath:/jdbc.properties", "classpath:/wechat.properties"})
public class AppConfig {
	
	final Logger logger = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) throws LifecycleException {
		
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(Integer.getInteger("port", 80));
		tomcat.getConnector();
		Context ctx = tomcat.addWebapp("", new File("src/main/webapp").getAbsolutePath());
		WebResourceRoot resources = new StandardRoot(ctx);
		resources.addPreResources(
				new DirResourceSet(resources, "/WEB-INF/classes", new File("target/classes").getAbsolutePath(), "/"));
		ctx.setResources(resources);
		tomcat.start();
		tomcat.getServer().await();
	}
	
	
	public static String APPID;
	public static String APPSECRET;
	//初始化静态参数-wechat
	@Configuration
	class WechatConfig{
		@Value("${wechat.appId}")
		private  String appId;
		
		@Value("${wechat.appSecret}")
		private  String appSecret;
		
		@PostConstruct
		void init() {
			
			AppConfig.APPID = appId;
			AppConfig.APPSECRET = appSecret;
		}
	}
	
	@Bean
	DataSource createDataSource(
			@Value("${jdbc.url}") String jdbcUrl,
			@Value("${jdbc.username}") String jdbcUsername,
			@Value("${jdbc.password}") String jdbcPassword) {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(jdbcUsername);
		config.setPassword(jdbcPassword);
		config.addDataSourceProperty("autoCommit", "false");
		config.addDataSourceProperty("connectionTimeout", "5");
		config.addDataSourceProperty("idleTimeout", "60");
		return new HikariDataSource(config);
	}
	
	@Bean
	SqlSessionFactoryBean createSqlSessionFactoryBean(@Autowired DataSource dataSource) {
		var sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		return sqlSessionFactoryBean;
	}
	
	@Bean
	JdbcTemplate createJdbcTemplate(@Autowired DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	
	@Bean
	PlatformTransactionManager createTxManager(@Autowired DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	
	@Bean
	WebMvcConfigurer createWebMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/static/**").addResourceLocations("/static/");
				registry.addResourceHandler("/wechat/**").addResourceLocations("/");
			}
		};
	}
	
	@Bean(name = "wechatOpenIdFilter")
	WechatOpenIdFilter createWechatOpenIdFilter() {
		return new WechatOpenIdFilter();
	}

	
	//shiro configuration
	@Bean(name = "shiroFilter")
	ShiroFilterFactoryBean createShiroFilterFactoryBean(@Autowired org.apache.shiro.mgt.SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		
		//配置权限-测试
//		filterChainDefinitionMap.put("/static/html/login.html", "anon");
//		filterChainDefinitionMap.put("/shiro/login", "anon");
//		filterChainDefinitionMap.put("/shiro/logout", "logout");
//		filterChainDefinitionMap.put("/static/html/user.html", "authc,roles[user]");
//		filterChainDefinitionMap.put("/static/html/admin.html", "authc,roles[admin]");
//		filterChainDefinitionMap.put("/static/html/list.html", "authc");
//		
//		filterChainDefinitionMap.put("/**", "authc");
		
		//配置基础信息
		shiroFilterFactoryBean.setLoginUrl("/static/html/login.html");
		shiroFilterFactoryBean.setSuccessUrl("/static/html/main.html");
		shiroFilterFactoryBean.setUnauthorizedUrl("/static/html/unauthorized.html");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}
	
	@Bean("securityManager")
	public DefaultWebSecurityManager securityManager(@Autowired Authenticator authenticator,
			@Autowired EhCacheManager cacheManager,@Autowired Collection<Realm> realms) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setCacheManager(cacheManager);
		securityManager.setAuthenticator(authenticator);
		securityManager.setRealms(realms);
		return securityManager;
	}
	
	@Bean("authenticator")
	public ModularRealmAuthenticator authenticator() {
		ModularRealmAuthenticator autehnticator = new ModularRealmAuthenticator();
		autehnticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
		return autehnticator;
	}
	
	@Bean("cacheManager")
	public EhCacheManager ehCacheManager() {
		EhCacheManager ehCacheManager = new EhCacheManager();
		ehCacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
		return ehCacheManager;
	}
	
	@Bean("lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}
	
//		@Bean
//		@DependsOn(value = "lifecycleBeanPostProcessor")
//		public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
//			return new DefaultAdvisorAutoProxyCreator();
//		}
	
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Autowired org.apache.shiro.mgt.SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}
	
	@Bean("realms")
	public Collection<Realm> realms(@Autowired ShiroRealm shiroRealm) {
		Collection<Realm> realms = new ArrayList<Realm>();
		realms.add(shiroRealm);
		return realms;
	}
	
	@Bean("shiroRealm")
	public ShiroRealm shiroRealm(@Autowired CredentialsMatcher credentialsMatcherMD5) {
		ShiroRealm shiroRealm = new ShiroRealm();
		shiroRealm.setCredentialsMatcher(credentialsMatcherMD5);
		return shiroRealm;
	}
	
	@Bean("credentialsMatcherMD5")
	public CredentialsMatcher credentialsMatcher() {
		HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
		credentialsMatcher.setHashAlgorithmName("MD5");
		credentialsMatcher.setHashIterations(1024);
		return credentialsMatcher;
	}
}
