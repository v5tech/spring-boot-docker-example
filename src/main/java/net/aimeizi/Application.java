package net.aimeizi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;

@SpringBootApplication
@RestController
public class Application {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<User> getList() {
        return jdbcTemplate.query("select * from users", (resultSet, rowNum) -> {
            User user = new User();
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
            user.setEnabled(resultSet.getInt("enabled"));
            return user;
        });
    }

    @RequestMapping("/info")
    public String info() {
        return "Greetings from Spring Boot in docker!";
    }

    @RequestMapping("/users")
    public List<User> users() {
        return getList();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}


@Data
@NoArgsConstructor
@AllArgsConstructor
class User implements Serializable {
    private String username;
    private String password;
    private Integer enabled;
}