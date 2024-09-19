package com.jsp.online_compiler.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
	public class GlobalExceptionHandler {

	    @ExceptionHandler(Exception.class)
	    public String handleException(Exception e, Model model) {
	        model.addAttribute("output", "An error occurred: " + e.getMessage());
	        return "index"; // Return to the same template
	    }
	}

