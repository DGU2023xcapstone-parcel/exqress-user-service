package com.example.userservice.controller;

import com.example.userservice.StatusEnum;
import com.example.userservice.dto.UserDto;
import com.example.userservice.kafkaDto.KafkaCreateUser;
import com.example.userservice.messagequeue.producer.KafkaProducer;
import com.example.userservice.messagequeue.topic.KafkaTopic;
import com.example.userservice.service.TokenServiceImpl;
import com.example.userservice.service.UserServiceImpl;
import com.example.userservice.vo.request.*;
import com.example.userservice.vo.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class UserController {
    private final UserServiceImpl userService;
    private final KafkaProducer kafkaProducer;
    private final TokenServiceImpl tokenService;
    @Operation(summary = "사용자 회원가입", description = "사용자가 회원가입을 시도합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/signUp")
    public ResponseEntity<ResponseData> createUser(@RequestBody RequestUser user){
        if(userService.isDuplicated(user.getEmail())){
            return new ResponseEntity<>(new ResponseData(StatusEnum.EXISTED.getStatusCode(), "이미 존재하는 회원입니다.", "", ""), HttpStatus.CONFLICT);
        }
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        KafkaCreateUser kafkaUser = userService.createKafkaUser(userDto);
        kafkaProducer.sendCreateUser(KafkaTopic.CREATE_USER, kafkaUser);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return new ResponseEntity<>(new ResponseData(StatusEnum.OK.getStatusCode(), "회원가입 성공", responseUser, ""), HttpStatus.OK);
    }

    @Operation(summary = "사용자 물품 조회", description = "사용자가 주문한 물품을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping("/user/parcels")
    public ResponseEntity<ResponseData> getQRInfoList(HttpServletRequest request){
        String userId = userService.getUserIdThroughRequest(request);
        return userService.getQRList(userId);
    }

    @Operation(summary = "사용자 로그인", description = "사용자가 로그인을 시도합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/signIn")
    public ResponseEntity<?> login(@RequestBody RequestLogin login, HttpServletRequest request, HttpServletResponse response){
        return userService.login(request, response, login);
    }


    @Operation(summary = "토큰 재발급", description = "Refresh Token을 이용하여 Access, Refresh 토큰을 재발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "401", description = "인가 기능이 확인되지 않은 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return tokenService.reissue(request, response);
    }

    @Operation(summary = "사용자 로그아웃", description = "사용자가 로그아웃을 시도합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "401", description = "인가 기능이 확인되지 않은 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/signOut")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
        String accessToken = tokenService.getAccessToken(request, response);

        return tokenService.logout(accessToken);
    }

    @Operation(summary = "사용자 개인정보 파기", description = "사용자가 자신의 개인정보를 파기합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "개인정보 파기 성공", content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "401", description = "인가 기능이 확인되지 않은 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/clear")
    public ResponseEntity<?> clearPrivateInformation(HttpServletRequest request, @RequestBody RequestInvoiceNo requestInvoiceNo){
        String userId = userService.getUserIdThroughRequest(request);
        return userService.clearPrivateInformation(userId, requestInvoiceNo.getInvoiceNo());
    }

    @Operation(summary = "물품 반송 요청", description = "사용자가 물품 반송 요청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "물품 반송 성공", content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "401", description = "인가 기능이 확인되지 않은 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/return")
    public ResponseEntity<?> requestReturnParcel(@RequestBody RequestQRcode requestQRcode){
        return userService.requestReturnParcel(requestQRcode.getQrId());
    }
}
