package br.com.victor3r.todolist.task;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.victor3r.todolist.user.IUserRepository;

@RestController
public class TaskController {
  @Autowired
  private ITaskRepository taskRepository;

  @Autowired
  private IUserRepository userRepository;

  @PostMapping("/users/{userId}/tasks")
  public ResponseEntity<TaskModel> create(@RequestBody TaskModel taskModel,
      @PathVariable(value = "userId") UUID userId) {

    var foundUser = this.userRepository.findById(userId);

    if (!foundUser.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }

    taskModel.setUser(foundUser.get());

    var createdTask = this.taskRepository.save(taskModel);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<HashMap<String, String>> handleException(ResponseStatusException e) {
    var hashMap = new HashMap<String, String>();

    hashMap.put("message", e.getReason());

    return ResponseEntity.status(e.getStatusCode()).body(hashMap);
  }

}
