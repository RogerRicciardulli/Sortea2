package com.example.sorteo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class HomeController {
	@GetMapping("/")
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView("home/index");
		return modelAndView;
	}
	
	@GetMapping("/index")
	public ModelAndView indexx() {
		ModelAndView modelAndView = new ModelAndView("home/index");
		return modelAndView;
	}
}
