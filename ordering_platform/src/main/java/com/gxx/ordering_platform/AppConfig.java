 package com.gxx.ordering_platform;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Security;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Service;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
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
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.terracotta.modules.ehcache.transaction.ReadCommittedClusteredSoftLock;

import com.gxx.ordering_platform.filter.WechatOpenIdFilter;
import com.gxx.ordering_platform.handler.OSMOrderingHandler;
import com.gxx.ordering_platform.interceptor.OSMWebSocketSession;
import com.gxx.ordering_platform.reamls.ShiroRealm;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import redis.clients.jedis.JedisPoolConfig;


@Configuration
@ComponentScan
@MapperScan("com.gxx.ordering_platform.mapper")
@EnableWebMvc
@EnableWebSocket
@EnableTransactionManagement
@PropertySource(value = {"classpath:/jdbc.properties", "classpath:/wechat.properties", 
		"classpath:/merchantNumber.properties", "classpath:/serviceNumber.properties",
		"classpath:/redis.properties", "classpath:/aliyunMsg.properties", "classpath:/btts.properties"}, encoding = "UTF-8")
public class AppConfig {
	
	final Logger logger = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) throws Exception {
		
		Tomcat tomcat = new Tomcat();
		
		Connector connector = tomcat.getConnector();
		connector.setURIEncoding("utf-8");
//		getSslConnector(connector);
		connector.setPort(80);
		
		Context ctx = tomcat.addWebapp("", new File("src/main/webapp").getAbsolutePath());
		WebResourceRoot resources = new StandardRoot(ctx);
		resources.addPreResources(
				new DirResourceSet(resources, "/WEB-INF/classes", new File("target/classes").getAbsolutePath(), "/"));
		ctx.setResources(resources);
		tomcat.start();
		tomcat.getServer().await();
	}
	
	{
		// 全局注册，只需一次，所以移动到appconfig中
		Security.addProvider(new BouncyCastleProvider());
	}
	
	private static void getSslConnector(Connector connector) throws IOException {
		
	    connector.setPort(8443);
	    connector.setSecure(true);
	    connector.setScheme("https");
	    connector.setAttribute("sslkeyAlias", "alias");
	    
	    String password = "";
	    String path = "";
	    try (InputStream inputStream = AppConfig.class.getClassLoader().getResourceAsStream("pfx-password.properties"); 
	    		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
		    try {
				password = bufferedReader.readLine();
				path = bufferedReader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    connector.setAttribute("keystorePass", password);
	    connector.setAttribute("keystoreType", "PKCS12");
	    connector.setAttribute("keystoreFile",
	            path);
	    connector.setAttribute("clientAuth", "false");
	    connector.setAttribute("sslProtocol", "TLS");
	    connector.setAttribute("maxThreads", "200");
	    connector.setAttribute("protocol", "org.apache.coyote.http11.Http11AprProtocol");
	    connector.setAttribute("SSLEnabled", true);
	    connector.setAttribute("ciphers", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
	    connector.setAttribute("SSLCipherSuite", "ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4");
	 }
	
	
	public static String APPID;
	public static String APPSECRET;
	public static String SERVICEAPPID;
	public static String SERVICEAPPSECRET;
	//初始化静态参数-wechat
	@Configuration
	class WechatConfig{
		@Value("${wechat.appId}")
		private  String appId;
		
		@Value("${wechat.appSecret}")
		private  String appSecret;
		
		@Value("${serviceNumber.appid}")
		private String serviceAppId;
		
		@Value("${serviceNumber.appSecret}")
		private  String serviceAppSecret;
		
		@PostConstruct
		void init() {
			
			AppConfig.APPID = appId;
			AppConfig.APPSECRET = appSecret;
			AppConfig.SERVICEAPPID = serviceAppId;
			AppConfig.SERVICEAPPSECRET = serviceAppSecret;
		}
	}
	
	
	@Bean
	WebSocketConfigurer createWebSocketConfigur(@Autowired OSMOrderingHandler osmOrderingHandler, @Autowired OSMWebSocketSession osmWebSocketSession) {
		// TODO Auto-generated method stub

		return new WebSocketConfigurer() {
			
			@Override
			public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//				registry.addHandler(osmOrderingHandler, "/websocketOrdering").addInterceptors(osmWebSocketSession);
				// 这里设置了允许跨域！
				// 没办法删除，因为Osmapp就是跨域的!
				registry.addHandler(osmOrderingHandler, "/websocketOrdering").addInterceptors(osmWebSocketSession).setAllowedOrigins("*");
			}
		};
	}
	
	@Bean
	DataSource createDataSource(
			@Value("${jdbc.url}") String jdbcUrl,
			@Value("${jdbc.username}") String jdbcUsername,
			@Value("${jdbc.password}") String jdbcPassword) throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(jdbcUsername);
		config.setPassword(jdbcPassword);
		config.addDataSourceProperty("autoCommit", "false");
		config.addDataSourceProperty("connectionTimeout", "5");
		config.addDataSourceProperty("idleTimeout", "60");
		return new HikariDataSource(config);
	}
	
	@SuppressWarnings("deprecation")
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
	
	@Bean(name="multipartResolver")
	public MultipartResolver multipartResolver(){
		return new CommonsMultipartResolver();
	}
	
	@Bean
	WebMvcConfigurer createWebMvcConfigurer(@Autowired HandlerInterceptor[] interceptors) {
		return new WebMvcConfigurer() {
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				// 前后端分离项目，已经不需要了
//				registry.addResourceHandler("/static/**").addResourceLocations("/static/");
				// 配置小程序域名校验文件？
				registry.addResourceHandler("/wechat/**").addResourceLocations("/");
			}

			@SuppressWarnings("deprecation")
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				List<String> includePathList = new ArrayList<String>();
				includePathList.add("/OSM/**");
				includePathList.add("/OSMAPP/**");
				includePathList.add("/bttsToken");
				// 添加到拦截器拦截队列中，同时前端请求修改，让其携带token
				// 这样操作是没用的，因为拦截器只针对controller，拦截器是居于aop的方法拦截的，而我们的websocket的连接没有controller，所以无效
				// 解决办法是在HttpSessionHandshakeInterceptor 中处理，是有需要拦截
				List<String> excludePathList = new ArrayList<String>();
				excludePathList.add("/OSM/login");
				excludePathList.add("/OSM/sendChangePWCheck");
				excludePathList.add("/OSM/realChangePWCheck");
				
				// WechatIsOpeningInterceptor
				List<String> isOpeningIncludePathList = new ArrayList<String>();
				isOpeningIncludePathList.add("/wechat/loggedIn/add");
				isOpeningIncludePathList.add("/wechat/loggedIn/order");
				List<String> isOpeningExcludePathList = new ArrayList<String>();
				
				
				for (var interceptor : interceptors) {
					if ("com.gxx.ordering_platform.interceptor.AuthInterceptor".equals(interceptor.getClass().getName())) {
						registry.addInterceptor(interceptor).excludePathPatterns(excludePathList).addPathPatterns(includePathList);
						continue;
					}
					if ("com.gxx.ordering_platform.interceptor.WechatIsOpeningInterceptor".equals(interceptor.getClass().getName())) {
						registry.addInterceptor(interceptor).excludePathPatterns(isOpeningExcludePathList).addPathPatterns(isOpeningIncludePathList);
						continue;
					}
					registry.addInterceptor(interceptor);
				}
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
	
	@Bean("jedisPoolConfig")
	public JedisPoolConfig createJedisPoolConfig(
			@Value("${redis.maxIdle}") int maxIdle,
			@Value("${redis.maxTotal}") int maxTotal,
			@Value("${redis.maxWaitMillis}") long maxWaitMillis,
			@Value("${redis.minEvictableIdleTimeMillis}") long minEvictableIdleTimeMillis,
			@Value("${redis.numTestsPerEvictionRun}") int numTestsPerEvictionRun,
			@Value("${redis.timeBetweenEvictionRunsMillis}") long timeBetweenEvictionRunsMillis,
			@Value("${redis.testOnBorrow}") boolean testOnBorrow,
			@Value("${redis.testWhileIdle}") boolean testWhileIdle) {
		
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		// 最大空闲数
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMaxTotal(maxTotal);
		jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
		jedisPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		jedisPoolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		jedisPoolConfig.setTestOnBorrow(testOnBorrow);
		jedisPoolConfig.setTestWhileIdle(testWhileIdle);
		
		return jedisPoolConfig;
	}
	
	@Bean("jedisConnectionFactory")
	public JedisConnectionFactory createJedisConnectionFactory(
			@Value("${redis.hostName}") String hostName,
			@Value("${redis.port}") int port,
			@Value("${redis.password}") String password,
			@Value("${redis.timeout}") long timeout,
			@Value("${redis.database}") int database,
			@Autowired JedisPoolConfig jedisPoolConfig) {
		
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(hostName);
		redisStandaloneConfiguration.setDatabase(database);
		redisStandaloneConfiguration.setPassword(password);
		redisStandaloneConfiguration.setPort(port);
		JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jedisPoolingClientConfigurationBuilder = 
				(JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
		jedisPoolingClientConfigurationBuilder.poolConfig(jedisPoolConfig);
		JedisClientConfiguration jedisClientConfiguration = jedisPoolingClientConfigurationBuilder.build();
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
		jedisConnectionFactory.afterPropertiesSet();
		
		return jedisConnectionFactory;
	}
	
	@Bean("stringRedisTemplate")
	public StringRedisTemplate initRedisTemplate(@Autowired JedisConnectionFactory jedisConnectionFactory) {		
		
		//自定义  Redis 序列化器
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		//定义 RedisTemplate 并设置连接工程
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(jedisConnectionFactory);
        //设置序列化器
        stringRedisTemplate.setDefaultSerializer(stringRedisSerializer);
        stringRedisTemplate.setKeySerializer(stringRedisSerializer);
        stringRedisTemplate.setValueSerializer(stringRedisSerializer);
        stringRedisTemplate.setHashKeySerializer(stringRedisSerializer);
        stringRedisTemplate.setHashValueSerializer(stringRedisSerializer);
  
		return stringRedisTemplate;
	}
}
