package com.example.chapter11.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;



@Configuration
public class DruidConfig {
    private Logger logger = LoggerFactory.getLogger(DruidConfig.class);

    @Value("${spring.datasource.url:#{null}}")
    private String dbUrl;

    @Value("${spring.datasource.username: #{null}}")
    private String username;

    @Value("${spring.datasource.password:#{null}}")
    private String password;

    @Value("${spring.datasource.driverClassName:#{null}}")
    private String driverClassName;

    @Value("${spring.datasource.initialSize:#{null}}")
    private Integer initialSize;

    @Value("${spring.datasource.minIdle:#{null}}")
    private Integer minIdle;

    @Value("${spring.datasource.maxActive:#{null}}")
    private Integer maxActive;

    @Value("${spring.datasource.maxWait:#{null}}")
    private Integer maxWait;

    @Value("${spring.datasource.timeBetweenEvictionRunsMillis:#{null}}")
    private Integer timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.minEvictableIdleTimeMillis:#{null}}")
    private Integer minEvictableIdleTimeMillis;

    @Value("${spring.datasource.validationQuery:#{null}}")
    private String validationQuery;

    @Value("${spring.datasource.testWhileIdle:#{null}}")
    private Boolean testWhileIdle;

    @Value("${spring.datasource.testOnBorrow:#{null}}")
    private Boolean testOnBorrow;

    @Value("${spring.datasource.testOnReturn:#{null}}")
    private Boolean testOnReturn;

    @Value("${spring.datasource.poolPreparedStatements:#{null}}")
    private Boolean poolPreparedStatements;

    @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize:#{null}}")
    private Integer maxPoolPreparedStatementPerConnectionSize;

    @Value("${spring.datasource.filters:#{null}}")
    private String filters;

    @Value("{spring.datasource.connectionProperties:#{null}}")
    private String connectionProperties;


    @Bean
    @Primary
    public DataSource dataSource(){
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setUrl(dbUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);

        if(initialSize != null) {
            dataSource.setInitialSize(initialSize);
        }
        if(minIdle != null) {
            dataSource.setMinIdle(minIdle);
        }
        if(maxActive != null) {
            dataSource.setMaxActive(maxActive);
        }
        if(maxWait != null) {
            dataSource.setMaxWait(maxWait);
        }
        if(timeBetweenEvictionRunsMillis != null) {
            dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        }
        if(minEvictableIdleTimeMillis != null) {
            dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        }
        if(validationQuery!=null) {
            dataSource.setValidationQuery(validationQuery);
        }
        if(testWhileIdle != null) {
            dataSource.setTestWhileIdle(testWhileIdle);
        }
        if(testOnBorrow != null) {
            dataSource.setTestOnBorrow(testOnBorrow);
        }
        if(testOnReturn != null) {
            dataSource.setTestOnReturn(testOnReturn);
        }
        if(poolPreparedStatements != null) {
            dataSource.setPoolPreparedStatements(poolPreparedStatements);
        }
        if(maxPoolPreparedStatementPerConnectionSize != null) {
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        }

        if(connectionProperties != null) {
            dataSource.setConnectionProperties(connectionProperties);
        }

        List<Filter> filters = new ArrayList<>();
        filters.add(statFilter());
        filters.add(wallFilter());
        dataSource.setProxyFilters(filters);

        return dataSource;

    }

    @Bean
    public ServletRegistrationBean druidServlet(){
        ServletRegistrationBean servletRegistrationBean = new
                ServletRegistrationBean(new StatViewServlet(), "/druid/*");

        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "admin");
        return servletRegistrationBean;

    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        return filterRegistrationBean;
    }

    @Bean
    public StatFilter statFilter(){
        StatFilter statFilter = new StatFilter();
        statFilter.setLogSlowSql(true); //slowSqlMillis用来配置SQL慢的标准，执行时间超过slowSqlMillis的就是慢。
        statFilter.setMergeSql(true); //SQL合并配置
        statFilter.setSlowSqlMillis(1000);//slowSqlMillis的缺省值为3000，也就是3秒。
        return statFilter;
    }


    @Bean
    public WallFilter wallFilter(){
        WallFilter wallFilter = new WallFilter();
        WallConfig config = new WallConfig();
        //允许执行多条SQL
        config.setMultiStatementAllow(true);
        wallFilter.setConfig(config);
        return wallFilter;
    }


}
