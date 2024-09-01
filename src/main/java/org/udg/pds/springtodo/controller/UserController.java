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
    userService.register(ru.username, ru.email, ru.password, ru.descripcio);
    return BaseController.OK_MESSAGE;

  }

  @GetMapping(path="/me")
  @JsonView(Views.Complete.class)
  public User getUserProfile(HttpSession session) {

    Long loggedUserId = getLoggedUser(session);

    return userService.getUserProfile(loggedUserId);
  }

  @GetMapping(path="/check")
  public User checkLoggedIn(HttpSession session) {
    return getUserProfile(session);
  }

  @PostMapping(path= "/me/addRecepta")
  public IdObject addRecepta(HttpSession session, @Valid @RequestBody ReceptaController.R_recepta recepta) {
      Long userId = getLoggedUser(session);


      return receptaService.addRecepta(recepta.nom, userId, recepta.descripcio, recepta.categories, recepta.imageUrl);
  }

  @GetMapping(path= "/me/receptesPujades")
  @JsonView(Views.Private.class)
  public Collection<Recepta> listAllMyReceptes(HttpSession session,
                                             @RequestParam(value = "from", required = false) Date from) {
      Long userId = getLoggedUser(session);
      return receptaService.getReceptes(userId);
  }

  @GetMapping(path = "/me/preferits")
  public Collection<Recepta> getReceptesPreferides(HttpSession session){
      Long userId = getLoggedUser(session);
      return userService.getReceptesPreferides(userId);
  }
  @PutMapping(path = "/me/preferits")
  public String addRemoveReceptesPreferides(@Valid @RequestBody R_recepta recepta, HttpSession session){
      Long userId = getLoggedUser(session);

      if (recepta.posar)
          userService.addReceptaPreferits(userId, recepta.id);
      else
          userService.removeReceptaPreferits(userId, recepta.id);
      return BaseController.OK_MESSAGE;
  }



    @GetMapping(path= "/me/receptesAltres")
    @JsonView(Views.Complete.class)
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

    @PutMapping(path = "/me")
    public User update(HttpSession httpSession, @RequestBody EditUser editUser){
      Long userId = getLoggedUser(httpSession);

      return userService.updateUser(userId, editUser.username, editUser.email, editUser.descripcio);
    }

    private static class R_recepta {
        @NotNull
        public Long id;
        @NotNull
        public Boolean posar;
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

    public String descripcio;
  }

    private static class EditUser {
        @NotNull
        public String username;
        @NotNull
        public String email;

        public String descripcio;
    }

}
