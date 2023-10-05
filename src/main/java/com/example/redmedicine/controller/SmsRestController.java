package com.example.redmedicine.controller;

import com.example.redmedicine.service.SmsService;
import com.example.redmedicine.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/sms/v1")
@RequiredArgsConstructor
public class SmsRestController {
    private final SmsService smsService;
    private final UserService userService;

    @PostMapping("/send")
    public Mono<Map> sendMsg(@RequestBody Map<String, String> body, HttpServletRequest req){
        String phoneNumber = body.get("userPhone");
        String userName = body.get("userName");
        Map<String,Object> result = smsService.sendMessage(phoneNumber);
        String authNumber = (String) result.get("authNumber");
        Mono<Map> mono = (Mono<Map>) result.get("mono");

        req.getSession().setAttribute("authNumber", authNumber);
        req.getSession().setAttribute("userName", userName);
        req.getSession().setAttribute("userPhone", phoneNumber);

        System.out.println("========================"+ userName);
        System.out.println("========================"+ phoneNumber);
        return mono;
    }
    @PostMapping("/msgSend")
    public Mono<Map> sendJoinMsg(@RequestBody Map<String, String> body, HttpServletRequest req){//제이슨으로 받은 값을 쏴주겠다
        String phoneNumber = body.get("confirm-num");//맵한테서 꺼내야지
        Map<String, Object> result = smsService.sendMessage("phoneNumber");//맵으로 받아서 result사용하기 위해
        String authNumber = (String)result.get("authNumber");//authNumber가 Object타입이었으니 String으로 형변환
        Mono<Map> mono = (Mono<Map>) result.get("mono");

        req.getSession().setAttribute("authNumber", authNumber);//세션을 통해서 인증번호 저장?
        req.getSession().setAttribute("userPhone", phoneNumber);

        System.out.println("========================"+ phoneNumber);
        return mono;
    }

    @PostMapping("/check")
    public String checkNumber(@RequestBody Map<String, String> param, HttpServletRequest req){
        String authNumber = (String)req.getSession().getAttribute("authNumber");
        String userName = (String) req.getSession().getAttribute("userName");
        String userPhone = (String) req.getSession().getAttribute("userPhone");
        String checkNumber = param.get("checkNumber");

        System.out.println("=========================="+ authNumber);
        System.out.println(checkNumber);
        System.out.println(userName);
        System.out.println(userPhone);

        String resultText = userService.findId(userName,userPhone);
        String result = "";

        System.out.println("==========================="+ resultText);
        if (resultText.contains("일치하는")){
            result = resultText;
        }else if(authNumber.equals(checkNumber) == false){
            result = "인증번호를 잘못 입력하셨습니다.";
        }else {
            result = "인증이 완료되었습니다.";
        }
        return result;
    }

    @PostMapping("/msgCheck")//전달받은 인증번호를 확인란으로 적고 확인 버튼을 눌렀을 때 실행될 것
    public String joinNumber(@RequestBody String checkNumber, HttpServletRequest req){
        String authNumber = (String)req.getSession().getAttribute("authNumber");//인증번호를 세션으로 받아오면 되었구나
        String userPhone = (String) req.getSession().getAttribute("userPhone");

        System.out.println("=========================="+ authNumber);
        System.out.println(userPhone);

        String result="";

        if (authNumber.equals(checkNumber)){
            result = "인증이 완료되었습니다.";
        }
        else {
            result = "인증번호를 잘못 입력하셨습니다.";
        }
        return result;
    }
}
