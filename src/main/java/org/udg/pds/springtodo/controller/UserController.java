package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.configuration.exceptions.ControllerException;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Recepta;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.entity.Views;
import org.udg.pds.springtodo.repository.ReceptaRepository;
import org.udg.pds.springtodo.service.ReceptaService;
import org.udg.pds.springtodo.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

// This class is used to process all the authentication related URLs
@RequestMapping(path="/users")
@RestController
public class UserController extends BaseController {

  @Autowired
  UserService userService;
  @Autowired
  ReceptaService receptaService;

  @Autowired
  ReceptaRepository receptaRepository;


  @PostMapping(path="/login")
  @JsonView(Views.Private.class)
  public User login(HttpSession session, @Valid @RequestBody LoginUser user) {
    checkNotLoggedIn(session);

    User u = userService.matchPassword(user.username, user.password);
    session.setAttribute("simpleapp_auth_id", u.getId());
    return u;
  }

  @PostMapping(path="/logout")
  @JsonView(Views.Private.class)
  public String logout(HttpSession session) {

    getLoggedUser(session);

    session.removeAttribute("simpleapp_auth_id");
    return BaseController.OK_MESSAGE;
  }

  @GetMapping(path="/{id}")
  @JsonView(Views.Public.class)
  public User getPublicUser(HttpSession session, @PathVariable("id") @Valid Long userId) {

    getLoggedUser(session);

    return userService.getUser(userId);
  }

  @DeleteMapping(path="/{id}")
  public String deleteUser(HttpSession session, @PathVariable("id") Long userId) {

    Long loggedUserId = getLoggedUser(session);

    if (!loggedUserId.equals(userId))
      throw new ControllerException("You cannot delete other users!");

    userService.deleteUser(userId);
    session.removeAttribute("simpleapp_auth_id");

    return BaseController.OK_MESSAGE;
  }


  @PostMapping(path="/register", consumes = "application/json")
  public String register(HttpSession session, @Valid  @RequestBody RegisterUser ru) {

    checkNotLoggedIn(session);
    userService.register(ru.username, ru.email, ru.password, ru.ubicacio);
    return BaseController.OK_MESSAGE;

  }

  @GetMapping(path="/me")
  @JsonView(Views.Complete.class)
  public User getUserProfile(HttpSession session) {

    Long loggedUserId = getLoggedUser(session);

    return userService.getUserProfile(loggedUserId);
  }

  @GetMapping(path="/check")
  public String checkLoggedIn(HttpSession session) {
    getLoggedUser(session);

    return BaseController.OK_MESSAGE;
  }

  @PostMapping(path= "/me/addRecepta")
  public IdObject addRecepta(HttpSession session, @Valid @RequestBody ReceptaController.R_recepta recepta) {
      Long userId = getLoggedUser(session);

      return receptaService.addRecepta(recepta.nom, userId, recepta.descripcio, recepta.categories);
  }

  @GetMapping(path= "/me/receptesPujades")
  @JsonView(Views.Private.class)
  public Collection<Recepta> listAllMyReceptes(HttpSession session,
                                             @RequestParam(value = "from", required = false) Date from) {
      Long userId = getLoggedUser(session);
      return receptaService.getReceptes(userId);
  }

    @GetMapping(path= "/me/receptesAltres")
    @JsonView(Views.Private.class)
    public Collection<Recepta> listAllReceptes(HttpSession session) {
        Long userId = getLoggedUser(session);
        Collection<Recepta> totes = receptaRepository.findAll();
        Collection<Recepta> altresReceptes = new ArrayList<>();
        for (Recepta i : totes)
        {
            if (i.getUsuari() == null || i.getUsuari().getId() != userId) altresReceptes.add(i);
        }

        return altresReceptes;
    }

    private static class LoginUser {
    @NotNull
    public String username;
    @NotNull
    public String password;
  }

  private static class RegisterUser {
    @NotNull
    public String username;
    @NotNull
    public String email;
    @NotNull
    public String password;

    public String ubicacio;
  }

}
