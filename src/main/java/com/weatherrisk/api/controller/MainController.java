package com.weatherrisk.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.weatherrisk.api.cnst.StormPathCnst;
import com.weatherrisk.api.service.HelloService;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class MainController {

    @Autowired
    private HelloService helloService;

    @RequestMapping("/")
    public String home(HttpServletRequest request, Model model) {

        String name = "the member of WeatherRisk";

        Account account = AccountResolver.INSTANCE.getAccount(request);
        if (account != null) {
            name = account.getGivenName();
            model.addAttribute(account);
        }

        model.addAttribute("name", name);

        return "main";
    }

    @RequestMapping("/restricted")
    public String restricted(HttpServletRequest request, Model model) {
        Account account = AccountResolver.INSTANCE.getAccount(request);
        String msg = helloService.sayHello(account);
        model.addAttribute("msg", msg);

        return "restricted";
    }

    @RequestMapping("/apis")
    @PreAuthorize("hasAuthority('" + StormPathCnst.GROUP_ADMIN_URL + "') and hasPermission('apis', 'view')")
    public String apis(HttpServletRequest request, Model model) {
    	return "redirect:swagger-ui.html";
    }
}
