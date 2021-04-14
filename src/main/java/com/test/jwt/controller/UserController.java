package com.test.jwt.controller;

import com.test.jwt.entity.TokenManager;
import com.test.jwt.entity.TokenManagerRepository;
import com.test.jwt.entity.User;
import com.test.jwt.entity.UserRepository;
import com.test.jwt.lib.JwtUtil;
import com.test.jwt.lib.UserStatus;
import com.test.jwt.request.AuthRequest;
import com.test.jwt.request.UserRequestDto;
import com.test.jwt.response.UserResponseDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repository;

    @Autowired
    TokenManagerRepository tokenManagerRepository;

    //단일 걸 때
//  @ApiOperation(value = "user", authorizations = @Authorization(value = "Bearer"))
    @ApiOperation(value = "전체 회원 찾기", notes = "전체 회원 찾기")
    @GetMapping
    public ResponseEntity<?> findAllUser() {
        return new ResponseEntity<>(repository.findAllUser(), HttpStatus.OK);
    }

    @ApiOperation(value = "자신의 정보 찾기", notes = "단일 회원 찾기")
    @GetMapping("/one")
    public ResponseEntity<?> findOneUser(HttpServletRequest request) {

        String token = request.getHeader("Authorization").substring(7);

        String userId = jwtUtil.extractUsername(token);

        return new ResponseEntity<>(repository.findOneUser(userId), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> updateOneUser(HttpServletRequest request, UserRequestDto.UserUpdateDto updateDto) {

        String token = request.getHeader("Authorization").substring(7);

        String userId = jwtUtil.extractUsername(token);
        System.out.println(userId);

        User user = repository.findOneByUserId(userId);

        user.setUserPassword("{noop}" + updateDto.getUserPassword());
        user.setEmail(updateDto.getEmail());

        User tmp = repository.save(user);
        System.out.println("tmp.userIdx : " + tmp.getIdx());
        System.out.println("tmp.userId : " + tmp.getUserId());

        return new ResponseEntity<>(repository.save(user), HttpStatus.OK);
    }


    @PostMapping("/login")
    public Map<String, String> generateToken(AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserId(), authRequest.getUserPassword()));
        } catch (Exception e) {
            throw new Exception("잘못된 아이디나 비밀번호 입니다.");
        }

        String accessToken = jwtUtil.generateToken(authRequest.getUserId());
        String refreshToken = jwtUtil.generateRefreshToken();

        TokenManager tokenManager = new TokenManager(accessToken,
                refreshToken,
                LocalDateTime.ofInstant(jwtUtil.extractExpiration(accessToken).toInstant(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(jwtUtil.extractExpiration(refreshToken).toInstant(), ZoneId.systemDefault()),
                repository.findIdxByUserId(authRequest.getUserId()));
        tokenManagerRepository.save(tokenManager);

        Map<String, String> tokenList = new HashMap<>();
        tokenList.put("accessToken", accessToken);
        tokenList.put("refreshToken", refreshToken);
        return tokenList;
    }

    @Transactional
    @PostMapping("/renew")
    public String renewAccessToken(@RequestHeader String refreshToken) {
        String token = refreshToken.substring(7);
        String userId = tokenManagerRepository.getUserIdByRefreshToken(token);
        if (userId == null || userId.equals("")) {
            return "Not vaild RefreshToken";
        } else {
            String newAccessToken = jwtUtil.generateToken(userId);

            tokenManagerRepository.renewAccessToken(newAccessToken, refreshToken, LocalDateTime.ofInstant(jwtUtil.extractExpiration(newAccessToken).toInstant(), ZoneId.systemDefault()));

            return newAccessToken;
        }
    }

    @PostMapping("/register")
    @ApiOperation(value = "사용자 등록", response = UserResponseDto.UserCommonDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "정상 저장 되었습니다."),
            @ApiResponse(code = 400, message = "잘못된 요청입니다.")

    })
    public ResponseEntity<?> registerUser(@Valid UserRequestDto.UserCommonDto commonDto, Errors errors) {
        if (errors.hasErrors()) {
            List<String> errorList = new ArrayList<>();
            for (ObjectError e : errors.getAllErrors()) {
                errorList.add(e.getDefaultMessage());
            }
            return new ResponseEntity<>(errorList, HttpStatus.BAD_REQUEST);
        }

        User user = new User(commonDto);
        user.setUserStatus(UserStatus.alive);
        return new ResponseEntity<>(new UserResponseDto.UserCommonDto(repository.save(user)), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        User user = repository.findOneByUserId(jwtUtil.extractUsername(token));
        user.setUserStatus(UserStatus.deleted);
        return new ResponseEntity<>(repository.save(user), HttpStatus.OK);
    }
}
