package payments.duo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import payments.duo.security.filters.JwtAuthenticationFilter;
import payments.duo.security.filters.JwtAuthorizationFilter;
import payments.duo.security.jwt.JwtTokenProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String SIGNIN_ENDPOINT = "/api/auth/signin";
    public static final String SIGNUP_ENDPOINT = "/api/auth/signup";

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CorsConfiguration corsConfiguration;

    public SecurityConfig(UserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider, CorsConfiguration corsConfiguration) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.corsConfiguration = corsConfiguration;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .cors().configurationSource(request -> corsConfiguration);
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
        http
                .authorizeRequests().antMatchers(SIGNIN_ENDPOINT).permitAll()
                .and()
                .authorizeRequests().antMatchers(SIGNUP_ENDPOINT).permitAll()
                .antMatchers("/api/v1/**").hasAnyAuthority("ROLE_CLIENT", "ROLE_ADMIN")
                .anyRequest().authenticated().and();
        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManagerBean(), jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
