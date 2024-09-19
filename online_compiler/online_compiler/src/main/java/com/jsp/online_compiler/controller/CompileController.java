package com.jsp.online_compiler.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.jsp.online_compiler.model.CodeExecutionRequest;

@Controller
public class CompileController {

    @GetMapping("/")
    public String index(Model model) {
        CodeExecutionRequest request = new CodeExecutionRequest();
        request.setLanguage("python"); // Default language
        model.addAttribute("codeExecutionRequest", request);
        return "index"; // Return the name of the Thymeleaf template
    }

    @PostMapping("/run")
    public String runCode(@ModelAttribute("codeExecutionRequest") CodeExecutionRequest request, Model model) {
        String output = executeCode(request.getLanguage(), request.getCode());
        model.addAttribute("output", output);
        model.addAttribute("codeExecutionRequest", request); // Preserve the input data
        return "index";
    }

    private String executeCode(String language, String code) {
        try {
            switch (language) {
                case "python":
                    return executePython(code);
                case "java":
                    return executeJava(code);
                case "cpp":
                    return executeCpp(code);
                case "ruby":
                    return executeRuby(code);
                default:
                    return "Unsupported language.";
            }
        } catch (Exception e) {
            return "Error executing code: " + e.getMessage();
        }
    }

    private String executePython(String code) throws Exception {
        return executeScript("temp_script.py", "python", code);
    }

    private String executeJava(String code) throws Exception {
        String filePath = "TempProgram.java";
        Files.write(Paths.get(filePath), code.getBytes());
        String output = executeCommand("javac TempProgram.java");
        if (output.isEmpty()) {
            output = executeCommand("java TempProgram");
        }
        new File(filePath).delete();
        new File("TempProgram.class").delete();
        return output;
    }

    private String executeCpp(String code) throws Exception {
        String filePath = "TempProgram.cpp";
        Files.write(Paths.get(filePath), code.getBytes());
        String output = executeCommand("g++ TempProgram.cpp -o TempProgram");
        if (output.isEmpty()) {
            output = executeCommand("./TempProgram");
        }
        new File(filePath).delete();
        new File("TempProgram").delete();
        return output;
    }

    private String executeRuby(String code) throws Exception {
        return executeScript("temp_script.rb", "ruby", code);
    }

    private String executeScript(String fileName, String interpreter, String code) throws Exception {
        Files.write(Paths.get(fileName), code.getBytes());
        String output = executeCommand(interpreter + " " + fileName);
        new File(fileName).delete();
        return output;
    }

    private String executeCommand(String command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command.split(" "));
        pb.redirectErrorStream(true);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output.toString();
    }
}
