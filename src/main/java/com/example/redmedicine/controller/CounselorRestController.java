package com.example.redmedicine.controller;

import com.example.redmedicine.domain.vo.Criteria;
import com.example.redmedicine.domain.vo.PageVo;
import com.example.redmedicine.domain.vo.SearchVo;
import com.example.redmedicine.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/counselor/*")
@RequiredArgsConstructor
public class CounselorRestController {
    private final BookingService bookingService;

    @GetMapping(value = {"/counselorBook/{page}"})
    public Map<String, Object> searchResult(@PathVariable("page") int page, SearchVo searchVo, HttpServletRequest req){
        Long userNumber = (Long)req.getSession().getAttribute("userNumber");

        Long userCNumber = bookingService.findCNumber(userNumber);
        Criteria criteria = new Criteria();
        criteria.setPage(page);
        PageVo pageVo = new PageVo(bookingService.searchTotal(searchVo, userCNumber), criteria);
        Map<String, Object> map = new HashMap<>();
        map.put("pageVo",pageVo);
        map.put("list",bookingService.findAll(criteria, searchVo, userCNumber));
        return map;
    }
}
