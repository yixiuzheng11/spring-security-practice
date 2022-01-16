package com.yixz.common.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.yixz.common.filter.JwtAthenticationFilter;
import com.yixz.common.filter.LoginFilter;
import com.yixz.common.provider.CustomDaoAuthenticationProvider;
import com.yixz.common.provider.CustomWebAuthenticationDetailsSource;
import com.yixz.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import javax.annotation.Resource;
import java.util.Properties;

/**
 * 描述
 *
 * @author YIXIUZHENG741
 * @date 2021年11月24日 16:42
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled =true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private UserServiceImpl userService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/js/**", "/css/**","/images/**",
                "/swagger-resources/**","/api-docs","/doc.html","/api-docs-ext","/webjars/**","/v2/**","/swagger-ui.html");
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //.antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/verCode").permitAll()
                .antMatchers("/login.html").permitAll()
                .anyRequest().authenticated()
                /*.and()
                .formLogin()
                .loginPage("/login.html").permitAll()*/
                .and()
                .csrf().disable();
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(jwtAthenticationFilter(), BasicAuthenticationFilter.class);
    }

    /**
     * 自定义登录过滤器，支持form和json传参
     * @author YIXIUZHENG741
     * @date 2021/11/26 10:19
     * @return com.yixz.sys.filter.LoginFilter
     */
    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setAuthenticationManager(authenticationManager());
        loginFilter.setAuthenticationDetailsSource(authenticationDetailsSource());
        loginFilter.setFilterProcessesUrl("/doLogin");
        return loginFilter;
    }

    /**
     * 自定义JWT过滤器
     * @author YIXIUZHENG741
     * @date 2021/11/26 10:19
     * @return com.yixz.sys.filter.LoginFilter
     */
    @Bean
    public JwtAthenticationFilter jwtAthenticationFilter() throws Exception {
        JwtAthenticationFilter jwtAthenticationFilter = new JwtAthenticationFilter(authenticationManager(), userService);
        return jwtAthenticationFilter;
    }

    /**
     * 登录校验
     * @author YIXIUZHENG741
     * @date 2021/11/26 10:19
     * @return com.yixz.sys.filter.LoginFilter
     */
    @Bean
    public CustomDaoAuthenticationProvider daoAuthenticationProvider(){
        CustomDaoAuthenticationProvider myDaoAuthenticationProvider = new CustomDaoAuthenticationProvider();
        myDaoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        myDaoAuthenticationProvider.setUserDetailsService(userService);
        return myDaoAuthenticationProvider;
    }

    @Bean
    public CustomWebAuthenticationDetailsSource authenticationDetailsSource() {
        CustomWebAuthenticationDetailsSource authenticationDetailsSource = new CustomWebAuthenticationDetailsSource();
        return authenticationDetailsSource;
    }

    /**
     * 验证码生成器
     * @author YIXIUZHENG741
     * @date 2021/12/20 17:26
     * @return com.google.code.kaptcha.Producer
     */
    @Bean
    Producer verifyCode() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "150");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
