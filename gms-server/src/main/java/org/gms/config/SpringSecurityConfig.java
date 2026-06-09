package org.gms.config;

import org.gms.aop.AuthEntryPointJwt;
import org.gms.aop.AuthTokenFilter;
import org.gms.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Spring Security 安全配置类
 * 配置JWT认证、无状态会话、CORS和权限控制
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
public class SpringSecurityConfig {
    /** 用户详情服务 */
    private final UserDetailsServiceImpl userDetailsService;

    /** 未授权请求处理器 */
    private final AuthEntryPointJwt unauthorizedHandler;

    /**
     * 构造函数
     *
     * @param userDetailsService 用户详情服务
     * @param unauthorizedHandler 未授权请求处理器
     */
    @Autowired
    public SpringSecurityConfig(UserDetailsServiceImpl userDetailsService, AuthEntryPointJwt unauthorizedHandler) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    /**
     * 创建JWT认证过滤器Bean
     *
     * @return JWT认证过滤器
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * 创建认证提供者Bean
     *
     * @return 认证提供者
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * 创建认证管理器Bean
     *
     * @param authConfig 认证配置
     * @return 认证管理器
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * 创建密码编码器Bean
     * 使用BCrypt算法进行密码加密
     *
     * @return BCrypt密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 创建安全过滤链Bean
     * 配置CORS、CSRF、会话策略和权限规则
     *
     * @param http HTTP安全配置
     * @return 安全过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling((config) -> config.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement((config) -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((config) -> config
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        // 允许访问前端web
                        .requestMatchers("/", "/static/**", "/index.html", "/assets/**").permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}