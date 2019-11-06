package my.project.user.controller;

import my.project.user.pojo.User;
import my.project.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private  UserService userService;
    @GetMapping("hello")
    @ResponseBody
    public  String test(){
        return "hello ssm";
    }

    @GetMapping("{id}")
    @ResponseBody
    public User queryUserById(@PathVariable("id") Long id){

        return this.userService.queryById(id);
    }

    @GetMapping("delete/{id}")
    public void deleteUserById(@PathVariable("id") Long id){
        this.userService.deleteById(id);
    }

    @GetMapping("all")
    public String all(Model model) {

        List<User> users = this.userService.queryAllUser();
        for (User u : users) {
            System.out.println(u.toString());
        }
        model.addAttribute("users",users);
        return "users";
    }
}

