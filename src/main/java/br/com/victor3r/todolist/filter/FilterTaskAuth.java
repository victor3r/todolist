package br.com.victor3r.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.victor3r.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var servletPath = request.getServletPath();

    if (!servletPath.contains("/tasks")) {
      filterChain.doFilter(request, response);
      return;
    }

    var authorizationHeader = request.getHeader("Authorization");

    if (authorizationHeader == null) {
      response.sendError(400, "MISSING_HEADER");
      return;
    }

    var encodedAuth = authorizationHeader.split(" ")[1];

    var decodedBytes = Base64.getDecoder().decode(encodedAuth);

    var userCredentials = new String(decodedBytes);

    var credentialsArray = userCredentials.split(":");

    var username = credentialsArray[0];
    var password = credentialsArray[1];

    var foundUser = this.userRepository.findByUsername(username);

    if (foundUser == null) {
      response.sendError(401, "INVALID_CREDENTIALS");
      return;
    }

    var result = BCrypt.verifyer().verify(password.toCharArray(), foundUser.getPassword());

    if (!result.verified) {
      response.sendError(401, "INVALID_CREDENTIALS");
      return;
    }

    request.setAttribute("user", foundUser);

    filterChain.doFilter(request, response);
  }

}
