package br.com.victor3r.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private IUserRepository userRepository;

  @PostMapping("")
  public ResponseEntity<UserModel> create(@RequestBody UserModel userModel) {

    var foundUser = this.userRepository.findByUsername(userModel.getUsername());

    if (foundUser != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "USER_ALREADY_EXISTS");
    }

    var hashedPassword = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());

    userModel.setPassword(hashedPassword);

    var createdUser = this.userRepository.save(userModel);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }
}
