package com.easymargining.launcher;

@Controller
@EnableAutoConfiguration
public class EasyMarginingLauncher {
	@RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(EasyMarginingLauncher.class, args);
    }
}