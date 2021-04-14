package com.test.jwt.config;

import com.google.common.collect.Lists;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Errors;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

@Configuration
@EnableSwagger2
@EnableAutoConfiguration
public class SwaggerConfig {
    @Bean
    //Swagger 설정을 도와주는 클래스
    public Docket api() {
        String version = "v1";
        String title = "";
        return new Docket(DocumentationType.SWAGGER_2)
                //false로 설정시 불필요한 응답코드와 설명 제거 가능
                .useDefaultResponseMessages(false)
                //Bean이 여러개일 시 명시해 줘야함, 화면 우측 상단의 리스트
                .groupName(version)
                .consumes(getConsumeContentTypes())
                .produces(getProduceContentTypes())
                //ApiSelectorBuilder생성, apis(), paths() 사용 가능하게 해줌
                .select()
                //api가 작성되 있는 패키지 지정, 컨트롤러가 있는 패키지 지정
                .apis(RequestHandlerSelectors.basePackage("com.test.jwt.controller"))
                //api에서 선택된 api중 path 지정하여 문서화 가능
                .paths(PathSelectors.any())
                .build()
                //SWAGGER에서 시큐리티 전역 설정 적용
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .apiInfo(apiInfo(title, version))
                .ignoredParameterTypes(Errors.class);
    }

    //swagger 좌측 상단 영역에 보일 정보
    private ApiInfo apiInfo(String title, String version) {
        return new ApiInfo(
                title,
                "Library REST API Swagger",
                version,
                "v1",
                new Contact("", "", ""),
                "",
                "",
                new ArrayList<>());
    }

    private Set<String> getProduceContentTypes() {
        Set<String> produces = new HashSet<>();
        produces.add("application/json;charset=UTF-8");
        return produces;
    }

    private Set<String> getConsumeContentTypes() {
        Set<String> consumes = new HashSet<>();
        consumes.add("application/json;charset=UTF-8");
        consumes.add("application/x-www-form-urlencoded");
        consumes.add("multipart/form-data");
        return consumes;
    }

    //시큐리티 전역 설정
    //APIKEY의 NAME과 밑의 SECURITYREFERENCE의 REFERENCE의 이름이 같아야 함
    private ApiKey apiKey() {
        return new ApiKey("Bearer", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return springfox
                .documentation
                .spi.service
                .contexts
                .SecurityContext
                .builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference("Bearer", authorizationScopes));
    }
}