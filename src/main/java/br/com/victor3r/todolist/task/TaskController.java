package br.com.victor3r.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.victor3r.todolist.user.UserModel;
import br.com.victor3r.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("")
  public ResponseEntity<TaskModel> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    var currentDate = LocalDateTime.now();

    if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())
        || taskModel.getEndAt().isBefore(taskModel.getStartAt())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "INVALID_DATE");
    }

    var currentUser = (UserModel) request.getAttribute("user");

    taskModel.setUser(currentUser);

    var createdTask = this.taskRepository.save(taskModel);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
  }

  @GetMapping("")
  public ResponseEntity<List<TaskModel>> list(HttpServletRequest request) {
    var currentUser = (UserModel) request.getAttribute("user");

    var userTasks = taskRepository.findByUser(currentUser);

    return ResponseEntity.status(HttpStatus.OK).body(userTasks);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TaskModel> update(@RequestBody TaskModel taskModel, @PathVariable UUID id,
      HttpServletRequest request) {
    var foundTask = this.taskRepository.findById(id);

    if (!foundTask.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND");
    }

    var currentUser = (UserModel) request.getAttribute("user");

    if (!foundTask.get().getUser().getId().equals(currentUser.getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "PERMISSION_DENIED");
    }

    Utils.copyNonNullProperties(taskModel, foundTask.get());

    var updatedTask = this.taskRepository.save(foundTask.get());

    return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
  }
}
